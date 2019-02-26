import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;

/**
 * @author Noah M. Jorgenson
 * CS7473 - Network Security
 */
public class PacketGenerator {
	final String name = "noah-packet-generator";
	final String version = "0.0.1a";
	
	private SimplePacketDriver driver;
	
	private String input_filename;

	public PacketGenerator() {
		System.out.println(this.name+" "+this.version);
		
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
		PacketGenerator generator = new PacketGenerator();
		
		if (args.length > 0){
			generator.set_input_filename(args[0]);
		}else{
			usage();
		}//end if/else arguments

		//Generate those packets!
		generator.init();
		
	}//end main

	private static void usage() {
		System.out.println("Usage: java PacketGenerator <input_filename>");
		System.exit(1);
	}//end usage
	
	private void set_input_filename(String in) {
		input_filename = in;
	}//end set_input_filename

	private void init() {
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
					if (packet.length>14){
						PacketSender handler = new PacketSender(packet);
					}//only ship out real packets
					
					//Allocate new byte array stream for next packet
					bytesOut = new ByteArrayOutputStream();
					
				}//end if empty line
			}//end while
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Invalid input file.");
		}//bad file
		
	}//end init

	/**
	 * Spawn new thread for packet sending
	 */
	private class PacketSender implements Runnable{
		
		private Thread thisThread;
		private byte[] packet;

		/**
		 * Thread for handling incoming packets
		 * @param packet
		 */
		public PacketSender(byte[] packet) {
			this.packet = packet;
			
			thisThread = new Thread(this);
			thisThread.start();
		}//end constructor

		public void run() {
			sendPacket();
		}//end run

		private void sendPacket() {
			
	       if(!driver.sendPacket(packet)){
	    	   System.out.println("Error sending packet.");
	       }//end if driver couldn't send packet
	        
		}//end sendPacket
		
	}//end PacketSender
	
	
	public static int hexPairToByte(String hexPair){		
		Integer hexPairInt = Integer.parseInt(hexPair, 16);
		byte hexPairByte = hexPairInt.byteValue();
		int hexPairByteInt = (int) hexPairByte & 0xFF;
		return hexPairByteInt;
	}//end hexPairToByte

}//end PacketParser
