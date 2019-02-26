import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author Noah M. Jorgenson
 * CS7473 - Network Security
 */
public class PacketParser {
	final String name = "noah-packet-parser";
	final String version = "0.0.1a";
	
	private SimplePacketDriver driver;
	private final ExecutorService packetPool; 
	
	private int packet_count = -1;
	private String input_filename;
	private String output_filename;
	private BufferedWriter fileOut;
	
	private String packet_type = "all";
	private boolean header_info_only = false;
	
	private InetAddress source_address;
	private InetAddress destination_address;
	
	private InetAddress OR_source_address;
	private InetAddress OR_destination_address;
	
	private InetAddress AND_source_address;
	private InetAddress AND_destination_address;
	
	private Integer source_port_start;
	private Integer source_port_end;
	
	private Integer destination_port_start;
	private Integer destination_port_end;
	
	final static int SECONDS = 1000;
	Vector<DatagramBuffer> buffers = new Vector<DatagramBuffer>();
	DatagramBufferWatcher buffersWatcher;

	public PacketParser() {
		System.out.println(this.name+" "+this.version);
		
		//Initialize the cache thread pool
		packetPool = Executors.newFixedThreadPool(30);
		
		//Initialize packet parsing driver
		this.driver = new SimplePacketDriver();
        String[] adapters=driver.getAdapterNames();
        if (driver.openAdapter(adapters[0])) System.out.println("Adapter is open: "+adapters[0]+"\n");
        
        //Instantiate command line argument variables
        
	}//end constructor


	/**
	 * Parse the passed arguments in a swtich statement in while
	 * @param args
	 */
	public static void main(String[] args) {
	
		//Create packet parser instance
		PacketParser parser = new PacketParser();
		
		if (args.length > 0){
			int curr = 0;
			while(curr < args.length){
				String arg = args[curr];
				switch(arg.charAt(1)){
					case 'c':
						parser.set_packet_count(args[++curr]);
						break;
					case 'r':
						parser.set_input_filename(args[++curr]);
						break;
					case 'o':
						parser.set_output_filename(args[++curr]);
						break;
					case 't':
						parser.set_packet_type(args[++curr]);
						break;
					case 'h':
						parser.set_header_bool();
						break;
					case 's':
						if(arg.equals("-src")){
							parser.set_source_address(args[++curr]);
						}else if(arg.equals("-sord")){
							parser.set_or_addrs(args[++curr], args[++curr]);
						}else if(arg.equals("-sandd")){
							parser.set_and_addrs(args[++curr], args[++curr]);
						}else if(arg.equals("-sport")){
							parser.set_source_port(args[++curr], args[++curr]);
						}
						break;
					case 'd':
						if(arg.equals("-dst")){
							parser.set_dest_address(args[++curr]);
						}else if(arg.equals("-dport")){
							parser.set_dest_port(args[++curr], args[++curr]);
						}
						break;
					default: 
						usage();
						break;
						
				}//end switch
				curr++; // Next argument
				
			}//end while curr_args
			
		}//end if arguments
		
		
		//Start her up!
		//parser.set_input_filename("./output.dat");
		//parser.set_output_filename("./output.dat");
		parser.init();
		
		
		
		
		
	}//end main

	private static void usage() {
		System.out.println("Usage: java PacketParser [options...]" +
				"\nOptions:" +
				"\n\t-c <count>\t\t\t\t\tExit after receiving count packets" +
				"\n\t-r <file>\t\t\t\t\tRead packets from file" +
				"\n\t-o <file>\t\t\t\t\tSave output to filename" +
				"\n\t-t <type>\t\t\t\t\tPrint only packets of the specified type where type is one of: eth, arp, ip, icmp, tcp or udp" +
				"\n\t-h\t\t\t\t\t\tPrint header info only" +
				"\n\t-src <address>\t\t\t\t\tPrint only packets with source address equal to address" +
				"\n\t-dst <address>\t\t\t\t\tPrint only packets with destination address equal to address" +
				"\n\t-sord <source_address> <destination_address>\tPrint only packets where source address or destination address match respective values" +
				"\n\t-sandd <source_address> <destination_address>\tPrint only packets where source address and destination address match respective values" +
				"\n\t-sport <port_start> <port_end>\t\t\tPrint only packets where the source port is in the range specified" +
				"\n\t-dport <port_start> <port_end>\t\t\tPrint only packets where the destination port is in the range specified");
		System.exit(1);
	}//end usage

	private boolean is_addressFilter() {		
		return ( (source_address!=null)||(destination_address!=null)||(OR_source_address!=null)||(OR_destination_address!=null)||(AND_source_address!=null)||(AND_destination_address!=null) );
	}//end is_addressFilter
	
	private boolean is_portFilter() {		
		return ( (source_port_start!=null)||(source_port_end!=null)||(destination_port_start!=null)||(destination_port_end!=null) );
	}//end is_addressFilter
	
	private void set_source_port(String portStart, String portEnd) {
		try{
			source_port_start = Integer.parseInt(portStart);
			source_port_end = Integer.parseInt(portEnd);
			if( (source_port_start > 65535 || source_port_start < 0) || (source_port_end > 65535 || source_port_end < 0) ){
				System.out.println("Invalid port value.\n");
				System.exit(1);
			}else if(source_port_start > source_port_end){
				System.out.println("Start port occurs after end port, use a valid range.\n");
				System.exit(1);
			}
		}catch(Exception e){
			System.out.println("Invalid port value.\n");
			System.exit(1);
		}//end try/catch
	}//end set_source_port
	
	private void set_dest_port(String portStart, String portEnd) {
		try{
			destination_port_start = Integer.parseInt(portStart);
			destination_port_end = Integer.parseInt(portEnd);
			if( (destination_port_start > 65535 || destination_port_start < 0) || (destination_port_end > 65535 || destination_port_end < 0) ){
				System.out.println("Invalid port value.\n");
				System.exit(1);
			}else if(destination_port_start > destination_port_end){
				System.out.println("Start port occurs after end port, use a valid range.\n");
				System.exit(1);
			}
		}catch(Exception e){
			System.out.println("Invalid port value.\n");
			System.exit(1);
		}//end try/catch
	}//end set_dest_port

	private void set_or_addrs(String srcAdd, String dstAdd) {
		try {
			OR_destination_address = InetAddress.getByName(dstAdd);
			OR_source_address = InetAddress.getByName(srcAdd);
		} catch (Exception e) {
			System.out.println("Invalid sord address values.\n");
			System.exit(1);
		}//end try/catch
	}//end set_or_addrs
	
	private void set_and_addrs(String srcAdd, String dstAdd) {
		try {
			AND_destination_address = InetAddress.getByName(dstAdd);
			AND_source_address = InetAddress.getByName(srcAdd);
		} catch (Exception e) {
			System.out.println("Invalid sandd address values.\n");
			System.exit(1);
		}//end try/catch
	}//end set_and_addrs

	private void set_source_address(String srcAdd) {
		try {
			source_address = InetAddress.getByName(srcAdd);
		} catch (Exception e) {
			System.out.println("Invalid source address.\n");
			System.exit(1);
		}//end try/catch
	}//end set_source_address
	
	private void set_dest_address(String dstAdd) {
		try {
			destination_address = InetAddress.getByName(dstAdd);
		} catch (Exception e) {
			System.out.println("Invalid destination address.\n");
			System.exit(1);
		}//end try/catch
	}//end set_dest_address

	private void set_packet_type(String type) {
		packet_type = type;
	}//end set_packet_type
	
	private void set_header_bool(){
		header_info_only = !header_info_only;
	}
	private void set_packet_count(String count){
		try{
			packet_count = Integer.parseInt(count);
		}catch(Exception e){
			System.out.println("Invalid count value.\n");
			System.exit(1);
		}//end try/catch
	}//end set_packet_count
	
	private void set_input_filename(String in) {
		input_filename = in;
	}//end set_input_filename
	
	private void set_output_filename(String out) {
		output_filename = out;
		try {
			fileOut = new BufferedWriter(new FileWriter(output_filename));
		} catch (Exception e) {
			System.out.println("Invalid output file.");
			System.exit(1);
		}
	}//end set_output_filename
	
	private void output(IDSTuple tuple, Object obj) {
		//Output to appropriate channel
		if(output_filename==null){
			if(!header_info_only){
				System.out.println(tuple.toString()+"\n"+obj.toString());
			}else{
				//print header info only
				System.out.println("Configure header info only output.");
			}
			
		}else{
			if(!header_info_only){
				String output = ((EthernetPacket) obj).toHexBlock();
				//Try to print to file
				try {
					fileOut = new BufferedWriter(new FileWriter(output_filename, true));
					fileOut.write(output);
				    fileOut.close();
				} catch (Exception e) {
					System.out.println("Invalid output file.");
					System.exit(1);
				}//end try/catch
			}else{
				//print header info only
				System.out.println("Configure header info only output.");
			}
			
		}//end if output to file or not
	}//end output

	private void init() {
		//start the watcher for timeouts
		startWatcher();
		
		if(input_filename==null){
			int num_packets = 0;
			while(num_packets != packet_count){
				byte [] packet=driver.readPacket();
				//packetPool.execute( new PacketHandler(packet) );
				packetPool.submit( new PacketHandler(packet) );
				num_packets++;
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
				if (is_addressFilter()) {
					ipPack = ipPack.address_filter(source_address,destination_address,OR_source_address,OR_destination_address,AND_source_address,AND_destination_address);
				}//end address filter
				
				if(ipPack != null){
					//For each protocol in IP
					if (ipPack.protocol.equals("TCP")) {
						
						TCPPacket tcpPack = new TCPPacket(packet);
						if (is_portFilter()) {
							tcpPack = tcpPack.port_filter(source_port_start, source_port_end, destination_port_start, destination_port_end);
						}//end port filter
						
						if(tcpPack!=null && tcpPack.is_type(packet_type)){
							
							
							IDSTuple tuple = assemblePacket(tcpPack);
							if(tuple!=null){
								byte[] assembled = tuple.PACKET.packet;
								tcpPack = new TCPPacket(assembled);
								output(tuple, tcpPack);
							}//if packet returned IDSTuple it completed with reassembled packet
							//output(tcpPack);
							
							
						}//end if port not fitered and is of packet type
						
					} else if (ipPack.protocol.equals("UDP")) {
						
						UDPPacket udpPack = new UDPPacket(packet);
						if (is_portFilter()) {
							udpPack = udpPack.port_filter(source_port_start, source_port_end, destination_port_start, destination_port_end);
						}//end port filter
						
						if(udpPack!=null && udpPack.is_type(packet_type)){
							
							
							IDSTuple tuple = assemblePacket(udpPack);
							if(tuple!=null){
								byte[] assembled = tuple.PACKET.packet;
								udpPack = new UDPPacket(assembled);
								output(tuple, udpPack);
							}//if packet returned IDSTuple it completed with reassembled packet
							//output(tcpPack);
							
							
						}//end if port not fitered and is of packet type
						
					} else if (ipPack.protocol.equals("ICMP")) {
						
						ICMPPacket icmpPack = new ICMPPacket(packet);
						if (!is_portFilter()) {
							if(icmpPack.is_type(packet_type)){
								
								
								IDSTuple tuple = assemblePacket(icmpPack);
								if(tuple!=null){
									byte[] assembled = tuple.PACKET.packet;
									icmpPack = new ICMPPacket(assembled);
									output(tuple, icmpPack);
								}//if packet returned IDSTuple it comleted reassembly
								//output(icmpPack);
								
								
							}//end if type
						}//end if not port filtering
						
					} else {/*System.out.println(ipPack.protocol);*/}
					//end IP protocols
				}//end if ip address not filtered
				
			} else if (ethPack.ethertype.equals("ARP")) {
				
				ARPPacket arpPack = new ARPPacket(packet);
				
				if (is_addressFilter()) {
					arpPack = arpPack.address_filter(source_address,destination_address,OR_source_address,OR_destination_address,AND_source_address,AND_destination_address);
				}
				if(arpPack!=null){
					if (!is_portFilter()) {
						if(arpPack.is_type(packet_type)){
							
							IDSTuple tuple = new IDSTuple(IDSTuple.ARP_SID, arpPack);
							output(tuple, arpPack);
							
						}//end if type
					}//end if not port filtering
						
				}//end if arp address not filtered
				
			} else {/*System.out.println(ethPack.ethertype);*/}
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
	
	private int calcHeaderLength(char headLenChar) {
		String headLenStr = String.valueOf(headLenChar);
		int headLenVal = Integer.parseInt(headLenStr, 16);
		//heaadLenVal = number of 32-bit words or 4 bytes
		return headLenVal * 4; //return byte length of header
	}//end calcHeaderLength
	
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
					while(iter.hasNext()){
						buffer = iter.next();
						buffer.checkTimeout();
						if(buffer.timeout){
							//return the IDSTuple
							IDSTuple tuple = buffer.generateTimeoutIDSTuple();
							output(tuple, buffer.first_fragment);
							
							//remove buffer for timing out
							iter.remove();
						}//end if timed out
					}//end while buffer in iter
				}//synchronized
			}//endless loop
		}//end run
	}//end DatagramBufferWatcher

}//end PacketParser
