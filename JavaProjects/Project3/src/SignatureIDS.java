import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;
import java.time.ZoneId;
/**
 * @author Noah M. Jorgenson
 * CS7473 - Network Security
 */
public class SignatureIDS {
	final String name = "noah-signature-ids";
	final String version = "0.0.1a";
	
	private SimplePacketDriver driver;
	private final ExecutorService packetPool = Executors.newFixedThreadPool(40);
	private RuleParser ruleParser;
	
	private int packet_count = -1;
	private String input_filename;
	private String rule_input_filename;
	private String output_filename = "output/ids-log.log";
	private boolean append_out = false;
	private BufferedWriter fileOut;
	
	final static int SECONDS = 1000;
	Vector<DatagramBuffer> buffers = new Vector<DatagramBuffer>();
	DatagramBufferWatcher buffersWatcher;

	public SignatureIDS() {
		System.out.println("Running Network Packet Parser.X");
		this.driver = new SimplePacketDriver();
		String[] networkAdapters = driver.getAdapterNames();			
		//if (smplPacketDriver.openAdapter(networkAdapters[0])) 
			//System.out.println("\nAdapter is open: "+ networkAdapters[0]+"\n"); 		
		// This code needed for running the program at the lab. Had to manually select the adapter to run the program on the ethernet. 
		
		int response = 0;		
		Scanner selAdapter = new Scanner (System.in);
		for (int i=0; i< networkAdapters.length; i++) {
			System.out.println("Device name in Java "+ "[" +i+ "]"+ "="+networkAdapters[i]); 
		}System.out.println("Enter the adapter number: ");
				response = 	selAdapter.nextInt();
		   if (driver.openAdapter(networkAdapters[response])){				   
			  System.out.println("\nAdapter [" + response + "] is open: "+networkAdapters[response]);
		   }
			
		   selAdapter.close();
	
	
	}//end constructor


	/**
	 * Parse the passed arguments in a swtich statement in while
	 * @param args
	 */
	public static void main(String[] args) {
	
		//Create packet parser instance
		SignatureIDS ids = new SignatureIDS();
		
		if (args.length > 0){
			int curr = 0;
			while(curr < args.length){
				String arg = args[curr];
				switch(arg.charAt(1)){
					case 'c':
						ids.set_packet_count(args[++curr]);
						break;
					case 'r':
						ids.set_rule_input_filename(args[++curr]);
						break;
					case 'i':
						ids.set_input_filename(args[++curr]);
						break;
					case 'o':
						ids.set_output_filename(args[++curr]);
						break;
					case 'a':
						ids.set_append_out();
						break;
					default: 
						usage();
						break;
				}//end switch
				curr++; // Next argument
				
			}//end while curr_args
			
		}//end if arguments
		
		//Start her up!
		ids.init();
		
	}//end main

	private static void usage() {
		System.out.println("Usage: java SignatureIDS [options...]" +
				"\nOptions:" +
				"\n\t-r <file>\tFilename containing rule definitions/signatures" +
				"\n\t-i <file>\tFilename containing packet bytes to read in" +
				"\n\t-o <file>\tFilename to output alerts matching signature" +
				"\n\t-c <count>\tExit after receiving count packets" +
				"\n\t-a \t\tAppend to IDS output log instead of overwriting");
		System.exit(1);
	}//end usage
	
	private void set_packet_count(String count){
		try{
			packet_count = Integer.parseInt(count);
		}catch(Exception e){
			System.out.println("Invalid count value.\n");
			System.exit(1);
		}//end try/catch
	}//end set_packet_count
	
	private void set_rule_input_filename(String in) {
		rule_input_filename = in;
		//parse rules
		ruleParser = new RuleParser(this.rule_input_filename);
	}//end set_input_filename
	
	private void set_input_filename(String in) {
		input_filename = in;
	}//end set_input_filename
	
	private void set_output_filename(String out) {
			System.out.println("outputfile");
		output_filename = out;
		try {
			fileOut = new BufferedWriter(new FileWriter(output_filename));
			fileOut.write("");
			fileOut.close();
		} catch (Exception e) {
			System.out.println("Invalid output file.");
			System.exit(1);
		}//end try/catch
	}//end set_output_filename
	
	private void set_append_out() {
		this.append_out = true;
	}//end set_append_out
	
	private void prepare_output_file(){
	
		if(!this.append_out){//clear output file
			try {	System.out.println("prepareout");
				fileOut = new BufferedWriter(new FileWriter(output_filename));
				fileOut.write("");
				fileOut.close();
			} catch (Exception e) {
				System.out.println("Invalid output file!!!!.");
				System.exit(1);
			}//end try/catch
		}//end if not appending output
	}//end prepare_output_file
	
	private void output(IDSTuple tuple, Object obj) {
		//string to write
		String infoOut = tuple.toString()+"\n"+obj.toString()+"\n";
		System.out.println(infoOut);
	}//end output
	
	private void log(IDSRule rule, IDSTuple tuple, Object obj) {
	    System.out.println("/Biglog");	
		String now = new Date().toString();
		String packetInfo = tuple.toString()+"\n"+obj.toString()+"\n";
		String logThis = String.format("%s %s\n%s", java.time.Clock.systemUTC().instant().atZone(ZoneId.systemDefault()), rule.log(), packetInfo);
		String outFile = rule.OPTION_LOGTO.isSet() ? rule.OPTION_LOGTO.value : this.output_filename;
		BufferedWriter out;
		//write to file
		try {
			out = new BufferedWriter(new FileWriter(outFile, true));
			out.write(logThis);
		    out.close();
		} catch (Exception e) {
			System.out.println("Invalid output file.");
			System.exit(1);
		}//end try/catch
	}//end log
	
	private void log(String message) {
			System.out.println("Small Log");
		String now = new Date().toString();
		String logThis = String.format("[%s] %s\n", now, message);
		String outFile = this.output_filename;
		BufferedWriter out;
		//write to file
		try {
			out = new BufferedWriter(new FileWriter(outFile, true));
			out.write(logThis);
		    out.close();
		} catch (Exception e) {
			System.out.println("Invalid output file.");
			System.exit(1);
		}//end try/catch
	}//end log
	
	private void output_IDS_IP(IDSTuple tuple, IPPacket ipPack) {
			System.out.println("output_IDS_IP");
		for (Iterator<IDSRule> iterator = ruleParser.IP_RULES.iterator(); iterator.hasNext();) {
			IDSRule rule = iterator.next();
			boolean match = ipPack.ruleCheck(rule, tuple);
			System.out.println("IPmatch " + match);
			if (match) {
				log(rule, tuple, ipPack);
			}//end if rule match
			
		}//end for each rule
	}//end output_IDS_IP
	
	private void output_IDS_ARP(IDSTuple tuple, ARPPacket arpPack){
		System.out.println("arp " );
		for (Iterator<IDSRule> iterator = ruleParser.ARP_RULES.iterator(); iterator.hasNext();) {
			
			IDSRule rule = iterator.next();
			boolean match = arpPack.ruleCheck(rule, tuple);
			System.out.println("arpmatch " + match);
			if (match) {
				log(rule, tuple, arpPack);
			}//end if rule match
			
		}//end for each rule
	}//end output_IDS_ARP
	
	private void output_IDS_TCP(IDSTuple tuple, TCPPacket tcpPack){
		
		//for each rule of this type, call the ruleFilter
		for (Iterator<IDSRule> iterator = ruleParser.TCP_RULES.iterator(); iterator.hasNext();) {
			
			IDSRule rule = iterator.next();
			boolean match = tcpPack.ruleCheck(rule, tuple);
			System.out.println("tcp match " + match);
			
			if (match) {
				log(rule, tuple, tcpPack);
			}//end if rule match
			
		}//end for each TCP+IP rule
	}//end output_IDS_TCP
	
	private void output_IDS_TCP(TCPPacket tcpPack) {
		output_IDS_TCP(new IDSTuple(IDSTuple.NOT_COMPLETED, tcpPack), tcpPack);
	}//end output_IDS_TCP

	private void output_IDS_UDP(IDSTuple tuple, UDPPacket udpPack){
		System.out.println("Udp!!!!!!!!!!!!!!!!!!!!!!! ");
		//for each rule of this type, call the ruleFilter
		for (Iterator<IDSRule> iterator = ruleParser.UDP_RULES.iterator(); iterator.hasNext();) {
			System.out.println("Iterator");
			IDSRule rule = iterator.next();
			boolean match = udpPack.ruleCheck(rule, tuple);
			if (match) {
				log(rule, tuple, udpPack);
			}//end if rule match
			
		}//end for each rule
		System.out.println("Udp endddddddd!!!!!!!!!!!!!!!!!!!!!!! ");
	}//end output_IDS_UDP
	
	private void output_IDS_UDP(UDPPacket udpPack) {
		output_IDS_UDP(new IDSTuple(IDSTuple.NOT_COMPLETED, udpPack), udpPack);
	}//end output_IDS_UDP
	
	private void output_IDS_ICMP(IDSTuple tuple, ICMPPacket icmpPack){
		//for each rule of this type, call the ruleFilter
		
		for (Iterator<IDSRule> iterator = ruleParser.ICMP_RULES.iterator(); iterator.hasNext();) {
			
			IDSRule rule = iterator.next();
			boolean match = icmpPack.ruleCheck(rule, tuple);
			if (match) {
				log(rule, tuple, icmpPack);
			}//end if rule match
			
		}//end for each rule
	}//end output_IDS_ICMP
	
	private void output_IDS_ICMP(ICMPPacket icmpPack) {
		output_IDS_ICMP(new IDSTuple(IDSTuple.NOT_COMPLETED, icmpPack), icmpPack);
	}//end output_IDS_ICMP

	private void init() {
		//prepare output file
		prepare_output_file();
		
		//start the watcher for timeouts
		startWatcher();
		
		if(input_filename==null){
			int num_packets = 0;
			while(num_packets != packet_count){
				byte [] packet=driver.readPacket();
				//packetPool.execute( new PacketHandler(packet) );
				packetPool.submit( new PacketHandler(packet) );
				num_packets++;
				System.out.println("numpkt");
			}//infinite loop never exits
		}else{
			//input_filename variable is set, so use it as input
			
			//read in each line
			//while line != '\n' just add to the byte array for this packet
			//for each pair of hexadecimal values calculate the byte value and add to byte array
			
			FileReader fr;
			BufferedReader br;
			ByteArrayOutputStream bytesOut;
			int num_packets = 0;
			
			try {
				fr = new FileReader(input_filename);
				br = new BufferedReader(fr);
				bytesOut = new ByteArrayOutputStream();
				
				String line;
				while((line = br.readLine()) != null) {
					String[] theline = line.split(" ");
					for (int i = 0; i < theline.length; i++) {
						String hexPair = theline[i];
						try{
							bytesOut.write(hexPairToByte(hexPair));
						}catch (Exception e) {/* Do nothing. */}
					}//end for each hexPair
					
					if(line.isEmpty()){
						//Packet ended, ship out current byte array stream for parsing
						byte[] packet = bytesOut.toByteArray();
						if (packet.length>14 && num_packets != packet_count){
							//packetPool.execute( new PacketHandler(packet) );
							packetPool.submit( new PacketHandler(packet) );
							num_packets++;
						}//only ship out real packets
						
						//Allocate new byte array stream for next packet
						bytesOut = new ByteArrayOutputStream();
						
					}//end if empty line
				}//end while
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Invalid input file.");
			}//bad file
		}//end if/else input_filename
		
	}//end init

	/**
	 * Spawn new thread for packet processings
	 */
	private class PacketHandler implements Runnable{
		
		private byte[] packet;

		/**
		 * Thread for handling incoming packets
		 * @param packet
		 */
		public PacketHandler(byte[] packet) {
			this.packet = packet;
		}//end constructor

		public void run() {
			handlePacket();
		}//end run

		synchronized private void handlePacket() {
			
	        //Determine the ethertype and protocol
			//Wrap in proper packet class
	        //packet class extract and assign the other relevant variables
			
			EthernetPacket ethPack = new EthernetPacket(packet);
			
			if (ethPack.ethertype.equals("IPv4")) {
				
				IPPacket ipPack = new IPPacket(packet);
				//for each rule where protocol matches (e.g. IP_RULES vector)
				//iterate through and call packet.ruleFilter(rule) and output
				//alert if ruleFilter passed. msg or generic alert to log file
				
				///** DEBUG **/output(new IDSTuple(IDSTuple.NOT_COMPLETED, ipPack),ipPack);
				//output filter by rules on ipPack
				//output_IDS_IP(new IDSTuple(IDSTuple.NOT_COMPLETED, ipPack), ipPack);
				//output_IDS_IP(new IDSTuple(IDSTuple.NOT_COMPLETED, ipPack), ipPack);
				
				if(ipPack != null){
					//For each protocol in IP
					if (ipPack.protocol.equals("TCP")) {
						TCPPacket tcpPack = new TCPPacket(packet);
						IDSTuple tuple = assemblePacket(tcpPack);
						/** DEBUG **/output(tuple,tcpPack);
						//output filter by rules on tcpPack
						output_IDS_TCP(tcpPack);
						if(tuple!=null){
							byte[] assembled = tuple.PACKET.packet;
							tcpPack = new TCPPacket(assembled);
							/** DEBUG **/output(tuple,tcpPack);
							//check reassembled packet
							output_IDS_TCP(tuple, tcpPack);
						}//if packet returned IDSTuple it completed with reassembled packet
					} else if (ipPack.protocol.equals("UDP")) {
						UDPPacket udpPack = new UDPPacket(packet);
						IDSTuple tuple = assemblePacket(udpPack);
						System.out.println("udp");
						///** DEBUG **/output(tuple,udpPack);
						//output filter by rules on udpPack
						output_IDS_UDP(udpPack);
						System.out.println("sid null");
						if(tuple!=null){
							byte[] assembled = tuple.PACKET.packet;
							udpPack = new UDPPacket(assembled);
							/** DEBUG **/output(tuple,udpPack);
							//check reassembled packet
							output_IDS_UDP(tuple, udpPack);
						}//if packet returned IDSTuple it completed with reassembled packet
					} else if (ipPack.protocol.equals("ICMP")) {
						ICMPPacket icmpPack = new ICMPPacket(packet);
						IDSTuple tuple = assemblePacket(icmpPack);
						///** DEBUG **/output(tuple,icmpPack);
						//output filter by rules on icmpPack
						output_IDS_ICMP(icmpPack);
						if(tuple!=null){
							byte[] assembled = tuple.PACKET.packet;
							icmpPack = new ICMPPacket(assembled);
							///** DEBUG **/output(tuple,icmpPack);
							//check reassembled packet
							output_IDS_ICMP(tuple, icmpPack);
						}//if packet returned IDSTuple it comleted reassembly
					} else {System.out.println("[ERROR] IP Protocol: "+ipPack.protocol);}
					//end IP protocols
				}//end if ip address not filtered
				
			} else if (ethPack.ethertype.equals("ARP")) {
				ARPPacket arpPack = new ARPPacket(packet);
				IDSTuple tuple = new IDSTuple(IDSTuple.ARP_SID, arpPack);
				///** DEBUG **/output(tuple,arpPack);
				//output filter by rules on arpPack
				output_IDS_ARP(tuple, arpPack);
			} else {System.out.println("[ERROR] Ethertype: "+ethPack.ethertype);}
			//end Ethernet ethertypes
	        
		}//end handlePacket
	}//end PacketHandler
	
	synchronized private DatagramBuffer findBuffer(IPPacket fragment){
		Iterator<DatagramBuffer> iter = buffers.iterator();
		DatagramBuffer bufferMatch = null;
		
		//check vector of DatagramBuffer for match
		DatagramBuffer buffer;
		while(iter.hasNext()){
			buffer = iter.next();
			if(buffer.match(fragment)){
				bufferMatch = buffer;
			}//end if match
		}//end while buffer in iter
		
		if (bufferMatch==null){
			//if buffer not found in vector create, add and return a new buffer
			DatagramBuffer newBuffer = new DatagramBuffer(fragment);
			buffers.add(newBuffer);
			bufferMatch = newBuffer;
		}//end if/else buffer matches fragment
		
		return bufferMatch;
	}//end findBuffer

	synchronized private IDSTuple assemblePacket(IPPacket fragment){
		//find buffer
		DatagramBuffer buffer = findBuffer(fragment);
		
		//add fragment
		IDSTuple tuple = buffer.addFragment(fragment);
		if(tuple!=null){
			buffers.remove(buffer);
		}//packet is completed, delete the buffer
		
		return tuple;
		
	}//assemblePacket
	
	private void startWatcher(){
		 buffersWatcher = new DatagramBufferWatcher();
	}//end startWatcher
	
	public static String getHexString(byte[] b) {
	  String result = "";
	  for (int i=0; i < b.length; i++) {
	    result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
	  }
	  return result;
	}//end getHexString
	
	public static int hexPairToByte(String hexPair){		
		Integer hexPairInt = Integer.parseInt(hexPair, 16);
		byte hexPairByte = hexPairInt.byteValue();
		int hexPairByteInt = (int) hexPairByte & 0xFF;
		return hexPairByteInt;
	}//end hexPairToByte
	
	private class DatagramBufferWatcher implements Runnable{
		/**
		 * Thread for watching for when DatagramBuffers timeout
		 */
		private Thread thisThread;
		private int interval = 1 * SECONDS;
		private boolean done = false;

		public DatagramBufferWatcher() {
			thisThread = new Thread(this);
			thisThread.start();
		}//end constructor

		synchronized public void run() {
			// Keep looping
			while(!done){
				// Put the timer to sleep
				try{ 
					Thread.sleep(interval);
				}catch (InterruptedException ioe){
					continue;
				}//end try/catch

				// Use 'synchronized' to prevent conflicts
				synchronized ( this ){
					//check datagram buffers for timeouts
					Iterator<DatagramBuffer> iter = buffers.iterator();
					DatagramBuffer buffer;
					System.out.println("iter.hasNext :" +((iter.hasNext())? "Not null" : "NULL") + " "+ buffers.toString() );
					while(iter.hasNext()){
						
						buffer = iter.next();
						buffer.checkTimeout();
						if(buffer.timeout){
							System.out.println("[DEBUG] Timeout!");
							//return the IDSTuple
							IDSTuple tuple = buffer.generateTimeoutIDSTuple();
							if (tuple.TYPE.equals("tcp")) {
								output(tuple, (TCPPacket) buffer.first_fragment);
								output_IDS_TCP(tuple, (TCPPacket) buffer.first_fragment);
							}else if (tuple.TYPE.equals("udp")) {
								output(tuple, (UDPPacket) buffer.first_fragment);
								output_IDS_UDP(tuple, (UDPPacket) buffer.first_fragment);
							}else if (tuple.TYPE.equals("icmp")) {
								output(tuple, (ICMPPacket) buffer.first_fragment);
								output_IDS_ICMP(tuple, (ICMPPacket) buffer.first_fragment);
							}else{
								log("[ERROR][DEBUG] Unrecognized protocol maybe?");
							}//end else/if IDS check the packet
							
							//remove buffer for timing out
							iter.remove();
						}//end if timed out
					}//end while buffer in iter
				}//synchronized
			}//endless loop
		}//end run
	}//end DatagramBufferWatcher

}//end PacketParser
