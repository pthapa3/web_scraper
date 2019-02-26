import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Vector;


public class DatagramBuffer {
	
	final static int MAX_PACKET_SIZE = 65536;
	final static int INFINITY = MAX_PACKET_SIZE+1;
	final static int SECONDS = 1000;
	final static int PACKET_LIFETIME = 5 * SECONDS; //seconds
	
	//identification values
	int identification;
	String protocol;
	InetAddress source_ip;
	InetAddress destination_ip;
	String source_mac;
	String destination_mac;
	
	public Vector<Hole> holeList = new Vector<Hole>();
	public Vector<EthernetPacket> fragments  = new Vector<EthernetPacket>();
	IPPacket first_fragment;
	byte[] packet = new byte[MAX_PACKET_SIZE];
	int headerOffset;
	int packetSize;
	int lastOctet;
	EthernetPacket reassembled;
	
	//checks
	boolean[] usedBytes = new boolean[MAX_PACKET_SIZE];
	boolean[] overlapBytes = new boolean[MAX_PACKET_SIZE];
	boolean overlap = false;
	boolean oversize = false;
	boolean timeout = false;
	long created_time = System.currentTimeMillis();
	
	boolean gotFirst = false;
	boolean gotLast = false;

	public DatagramBuffer(IPPacket firstFragment) {
		this.identification = firstFragment.identification;
		this.protocol = firstFragment.protocol;
		this.source_mac = firstFragment.getSrcMAC();
		this.destination_mac = firstFragment.getDstMAC();
		this.source_ip = firstFragment.source_ip;
		this.destination_ip = firstFragment.destination_ip;
		
		this.first_fragment = firstFragment;
		
		//intialize hole descriptor list
		this.holeList.add( new Hole(0,INFINITY) );
		
		/*
		//initialize packet with the header data
		this.headerOffset = firstFragment.IP_PAYLOAD_START;
		System.arraycopy(firstFragment.packet, 0, this.packet, 0, this.headerOffset);
		*/
		this.headerOffset = firstFragment.MAX_HEADER_SIZE;
		
		//add fragment seperately so we can get return from addFragment()
		
	}//end constructor
	
	public IDSTuple generateIDSTuple(){
		int sid = -1;
		
		if(!overlap && !oversize){
			sid = IDSTuple.CORRECT_SID;
		}//end SID = 1
		else if(oversize){
			sid = IDSTuple.OVERSIZE_SID;
		}
		else if(overlap){
			sid = IDSTuple.OVERLAP_SID;
		}//end if/else
		
		//construct reassembled packet
		this.reassembled = new IPPacket(packet);
		
		return new IDSTuple(sid, reassembled, fragments);
	}//end generateIDSTuple
	
	public IDSTuple generateOversizeIDSTuple(){
		return new IDSTuple(IDSTuple.OVERSIZE_SID, this.first_fragment, fragments);
	}//end oversized
	
	public IDSTuple generateTimeoutIDSTuple(){
		return new IDSTuple(IDSTuple.TIMEOUT_SID, this.first_fragment, fragments);
	}//end timedOut
	
	public void checkTimeout(){
		long now = System.currentTimeMillis();
		if( (now - created_time) >= PACKET_LIFETIME ){
			timeout = true;
		}//end if packet older than lifetime set timeout boolean
	}//end keepAlive
	
	private void printHoleList() {
		Vector<Hole> tempHoleList = new Vector<Hole>(holeList);
		for(Hole hole : tempHoleList){
			System.out.print(hole+"; ");
		}//end for each hole in holeList
		System.out.println("");
	}//end printHoleList

	public boolean match(IPPacket fragment){
	//Match identification, protocol and src/dst ips
		if ( fragment.identification==identification && 
				fragment.protocol.equals(protocol) &&
				fragment.source_ip.equals(source_ip) &&
				fragment.destination_ip.equals(destination_ip) &&
				fragment.getSrcMAC().equals(source_mac) &&
				fragment.getDstMAC().equals(destination_mac)){
			return true;
		}else{
			return false;
		}//end check matching values
	}//end match
	
	synchronized public IDSTuple addFragment(IPPacket fragment) {
		//returns true if last fragment received, otherwise false
		
		//System.out.println("");
		//printHoleList();
		//System.out.println("trying to add fragment");
		
		//add fragment to fragment list
		fragments.add(fragment);
		
		System.out.println("1");
		
		boolean lastFragment = fragment.isLastFragment();
		boolean firstFragment = fragment.isFirstFragment();
		if(firstFragment){ //setup the header which is based on the first fragment (i.e. offset = 0)
			System.out.println("2");
			gotFirst = true;
			headerOffset = fragment.IP_PAYLOAD_START;
			System.arraycopy(fragment.packet, 0, this.packet, 0, fragment.IP_PAYLOAD_START);
			
			if(!gotLast){//if you didn't get last packet yet, reformat it
				//System.out.println(this.packet.length);
				//System.out.println( "from " + (fragment.MAX_HEADER_SIZE+1) + " for this many bytes: " + (MAX_PACKET_SIZE-fragment.MAX_HEADER_SIZE-1) );
				System.arraycopy(this.packet, fragment.MAX_HEADER_SIZE+1, this.packet, fragment.IP_PAYLOAD_START, MAX_PACKET_SIZE-fragment.MAX_HEADER_SIZE-1);	
			}
			
		}
		if(lastFragment){ //setup the last octet value
		System.out.println("3");
			gotLast = true;
			lastOctet = fragment.last;	
		}
		
		if(gotFirst && gotLast){
			packetSize = headerOffset + lastOctet;
			System.out.println("4");
			if(packet.length > packetSize){
			
				//then adjust arrays for packet and checks based on the now known size
				byte[] newPacket = new byte[packetSize];
				boolean[] newUsed = new boolean[packetSize];
				boolean[] newOverlap = new boolean[packetSize];
				//System.arraycopy(packet, fragment.last, newPacket, 0, fragment.last);
				//System.arraycopy(usedBytes, fragment.last, newUsed, 0, fragment.last);
				//System.arraycopy(overlapBytes, fragment.last, newOverlap, 0, fragment.last);
				System.arraycopy(packet, 0, newPacket, 0, packetSize);
				System.arraycopy(usedBytes, 0, newUsed, 0, packetSize);
				System.arraycopy(overlapBytes, 0, newOverlap, 0, packetSize);
				this.packet = newPacket;
				this.usedBytes = newUsed;
				this.overlapBytes = newOverlap;
			}//end check to not go array out of bounds
			
			if(packetSize > MAX_PACKET_SIZE){
				oversize = true;
				return generateOversizeIDSTuple();
			}//set oversize
			
		}//if we have first and last we can form original datagram
		
		/**
		 *  FRAGMENT PROCESSING ALGORITHM
		 */
		Vector<Hole> tempHoleList = new Vector<Hole>(holeList); 
		for (Hole hole : tempHoleList) {
			if( fragment.first <= hole.last && fragment.last >= hole.first){
				System.out.println("5");
				//System.out.println(String.format("relevant fragment <%s, %s>", fragment.first, fragment.last));
				//write out the fragment data to the reassembled datagram buffer
				//if you write data to a non empty space then overlapping occured, set boolean for overlap = true; SID = 3
				writeData(fragment);
				
				//delete current hole descriptor entry from hole descriptor list
				holeList.remove(hole);
				
				//determine if new hole descriptors necessary
				if (fragment.first > hole.first){
					//new_hole = <new_hole.first=hole.first, new_hole.last=fragment.first-1>
					//System.out.println("adding new hole since fragment started before hole start");
					holeList.add( new Hole(hole.first, fragment.first-1) );
				}
				if (fragment.last < hole.last){
					//new_hole = <new_hole.first=fragment.last+1, new_hole.last=hole.last>
					//System.out.println("adding new hole since fragment ended before hole end");
					holeList.add( new Hole(fragment.last+1, hole.last) );
				}
			}//end if relevant hole
		}//end for each hole in hole descriptor list
		//printHoleList();
		
		
		if(lastFragment){
			System.out.println("lastFragment");
			//since we know size we can discard the hole descriptor whose last value = infinity or whatever
			//delete descriptor where last = infinity
			tempHoleList = new Vector<Hole>(holeList);
			for(Hole holeCheck : tempHoleList){
				if(holeCheck.last==INFINITY){
					holeList.remove(holeCheck);
					//System.out.println(holeCheck+" removed (infinity hole)");
				}//end hole ending with infinity
			}
		}//end if last fragment
		
		//System.out.println("holes left: "+holeList.size());
		//System.out.println("");
		if (holeList.size() == 0){ //if hole descriptor list is empty
			//datagram complete
			
			return generateIDSTuple(); //return the assembled datagram tuple
		}else if (timeout) {
			//timed out (120 seconds currently)
			
			return generateTimeoutIDSTuple();
		}else{ //else hole list not empty, not timeout, ergo packet not fully reconstructed
			System.out.println("Null");
			return null; // packet not completed	
		}//end if/else no more holes
		
	}//end addFragment

	private void writeData(IPPacket fragment) {
		//write out the fragment data to the reassembled datagram buffer
		//if you write data to a non empty space then overlapping occured, set boolean for overlap = true; SID = 3
		//System.arraycopy(fragment.getData(), 0, packet, fragment.first, fragment.last);
		byte[] fragmentData = fragment.getData();
		int firstOctet = fragment.first;
		for (int i = 0; i < fragmentData.length; i++) {
			int place = this.headerOffset + firstOctet + i;
			
			if(!(place >= MAX_PACKET_SIZE)){ //dont try to do crazy shit like check outside boundaries it breaks stuff
				if(usedBytes[place]){
					packet[place] = fragmentData[i];
					overlap = true;
					overlapBytes[place] = true;
				}else{
					packet[place] = fragmentData[i];
					usedBytes[place] = true;
				}//end if/else bytes already written
			}
		}//end for each byte in fragment.getData()
	}//end writeData
	
	public static int bytesToDecimal(byte[] b){
		BigInteger bigInt = new BigInteger(1, b);
		return Integer.parseInt(bigInt.toString());
	}//end toBitString

}//end DatagramBuffer
