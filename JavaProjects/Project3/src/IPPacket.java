import java.net.InetAddress;
import java.net.UnknownHostException;


public class IPPacket extends EthernetPacket {
	
	//http://www.tcpipguide.com/free/t_IPDatagramGeneralFormat.htm
	
	final int MAX_HEADER_SIZE = 60 + ETHER_PAYLOAD_START;
	
	//ipv4 packet locations
	final int IP_PACKET_START = ETHER_PAYLOAD_START;
	
	final int IP_VER_HL_LEN = 1;
	
	//TOS and ECN
	final int IP_TOSECN = IP_PACKET_START + (int)(8/BITS_IN_BYTE);
	final int IP_TOSECN_LEN = 1;
	//Total Length
	final int IP_LENGTH = IP_PACKET_START + (int)(16/BITS_IN_BYTE);
	final int IP_LENGTH_LEN = 2;
	//Identification
	final int IP_IDENT = IP_PACKET_START + (int)(32/BITS_IN_BYTE);
	final int IP_IDENT_LEN = 2;
	//Flags+Fragment Offset
	final int IP_FLAG_FRAG = IP_PACKET_START + (int)(48/BITS_IN_BYTE);
	final int IP_FLAG_FRAG_LEN = 2;
	final int FRAG_OFFSET_MULT = 8;
	//TTL
	final int IP_TTL = IP_PACKET_START + (int)(64/BITS_IN_BYTE);
	final int IP_TTL_LEN = 1;
	//Protocol
	final int IP_PROTOCOL_TYPE = IP_PACKET_START + (int)(72/BITS_IN_BYTE); //14+9=23
	final int IP_PROTOCOL_TYPE_LEN = 1;
	//Src/Dst Addr
	final int IP_SRC = IP_PACKET_START + (int)(96/BITS_IN_BYTE); //14+12=26
	final int IP_ADDR_LEN = 4;
	final int IP_DST = IP_SRC + IP_ADDR_LEN; //14+16=30
	
	int IP_PAYLOAD_START;
	int IP_PAYLOAD_END;

	final static int MINIMUM_HEADER_LENGTH = 5; //minimum header length in 32-bit words (IHL field); 5 * 32 = 160 bytes (minimum size header bytes)
	//ip protocols
	final String TCP = "06";
	final String UDP = "11";
	final String ICMP = "01";
	
	//private variables
	public int header_length;
	public int header_length_bytes;
	public int tos;
	public int total_length;
	public int identification;
	public String flags_and_fragment_offset;
	public String flags;
	public boolean reserved_bit;
	public boolean dont_fragment_bit;
	public boolean more_fragments_bit;
	
	public int fragment_offset;
	public int time_to_live;
	
	//values for fragment reassembly
	public int data_length;
	public int first;
	public int last;
	
	public String protocol;
	public InetAddress source_ip;
	public InetAddress destination_ip;
	public int number_options;
	
	//options
	public boolean rr_opt;
	public boolean eol_opt;
	public boolean nop_opt;
	public boolean ts_opt;
	public boolean sec_opt;
	public boolean esec_opt;
	public boolean lsrr_opt;
	//public boolean lsrre_opt; //unable to find corresponding number (not in IANA list)
	public boolean ssrr_opt;
	public boolean satid_opt;
	
	
	public IPPacket(byte[] packet) {
		super(packet);
		this.packet_type = "ip";
		parse();
	}//end constructor
	
	public String toString() {
		String out = super.toString();
		
		out+=outln("Header Length: "+header_length_bytes);
		out+=outln("TOS: "+tos);
		out+=outln("Total Length: "+total_length);
		out+=outln("Data Length: "+data_length); 
		out+=outln("Identification: "+identification);
		out+=outln("FlagFrag Binary: "+flags_and_fragment_offset);
		out+=outln("Flags: "+flags);
		out+=outln("Last Fragment?: "+this.isLastFragment());
		out+=outln("Fragment Offset: "+fragment_offset);
		out+=outln("First octet: "+first);
		out+=outln("Last octet: "+last);
		out+=outln("TTL: "+time_to_live);
		out+=outln("Payload Start: "+IP_PAYLOAD_START);
		out+=outln("Protocol: "+protocol);
		out+=outln("Source Address: "+source_ip);
		out+=outln("Destination Address: "+destination_ip);
		out+=outln("Number of options: "+number_options);
		
		return out;
	}//end toString
	
	public boolean is_type(String type){
		return ( type.equals("all") || type.equals("eth") || type.equals("ip") );
	}//end is_type
	
	private void parse(){
		byte[] verHL = new byte[IP_VER_HL_LEN];
		byte[] tosECN = new byte[IP_TOSECN_LEN];
		byte[] length = new byte[IP_LENGTH_LEN];
		byte[] ident = new byte[IP_IDENT_LEN];
		byte[] flagFrag = new byte[IP_FLAG_FRAG_LEN];
		byte[] ttl = new byte[IP_TTL_LEN];
		byte[] ipProt = new byte[IP_PROTOCOL_TYPE_LEN];
		byte[] srcAddr = new byte[IP_ADDR_LEN];
		byte[] dstAddr = new byte[IP_ADDR_LEN];
		
        System.arraycopy(packet, IP_PACKET_START, verHL, 0, IP_VER_HL_LEN);
        String verHLStr = driver.byteToHex(verHL[0]);
        header_length_bytes = calcHeaderLength(verHLStr.charAt(1));
        
        System.arraycopy(packet, IP_TOSECN, tosECN, 0, IP_TOSECN_LEN);
        parseTOS(tosECN);
        System.arraycopy(packet, IP_LENGTH, length, 0, IP_LENGTH_LEN);
        total_length = bytesToDecimal(length);
        data_length = total_length - header_length_bytes;
        System.arraycopy(packet, IP_IDENT, ident, 0, IP_IDENT_LEN);
        identification = bytesToDecimal(ident);
        System.arraycopy(packet, IP_FLAG_FRAG, flagFrag, 0, IP_FLAG_FRAG_LEN);
        parseFlagFrag(flagFrag);
        System.arraycopy(packet, IP_TTL, ttl, 0, IP_TTL_LEN);
        time_to_live = bytesToDecimal(ttl);
        //toBitString
        
        
        IP_PAYLOAD_START = IP_PACKET_START + header_length_bytes;
        
        System.arraycopy(packet, IP_PROTOCOL_TYPE, ipProt, 0, IP_PROTOCOL_TYPE_LEN);
        String ipProtNum = getHexString(ipProt);
        if(ipProtNum.equals(TCP)){
        	protocol = "TCP";
        }else if(ipProtNum.equals(UDP)){
        	protocol = "UDP";
        }else if(ipProtNum.equals(ICMP)){
        	protocol = "ICMP";
        }else{
        	protocol = "Unrecognized IP protocol: "+ipProtNum;
        }
        
        System.arraycopy(packet, IP_SRC, srcAddr, 0, IP_ADDR_LEN);
        System.arraycopy(packet, IP_DST, dstAddr, 0, IP_ADDR_LEN);
        try {
			destination_ip = InetAddress.getByAddress(dstAddr);
			source_ip = InetAddress.getByAddress(srcAddr);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}//end try/catch
		
		//options
		number_options = header_length - MINIMUM_HEADER_LENGTH;
		parseOptions();
        
        
	}//end parse
	
	private void parseOptions() {
		// TODO Auto-generated method stub
		
	}//end parseOptions

	private void parseTOS(byte[] tosAndECNBytes) {
		String binaryTOSAndECN = bytesToBinary(tosAndECNBytes);
		String binaryTOS = binaryTOSAndECN.substring(0,6);
		tos = Integer.parseInt(binaryTOS, 2);
	}//end parseFlagFrag

	private void parseFlagFrag(byte[] flagFrag) {
		String binaryFlagFrag = bytesToBinary(flagFrag);
		flags_and_fragment_offset = binaryFlagFrag;
		
		flags = binaryFlagFrag.substring(0, 3);
		reserved_bit = flags.charAt(0)=='1';
		dont_fragment_bit = flags.charAt(1)=='1';
		more_fragments_bit = flags.charAt(2)=='1';
		
		fragment_offset = Integer.parseInt(binaryFlagFrag.substring(3), 2);
		first = (fragment_offset * FRAG_OFFSET_MULT);
		last = (fragment_offset * FRAG_OFFSET_MULT) + data_length;
	}//end parseFlagFrag
	
	public boolean isLastFragment(){
		return flags.charAt(2)=='0';
	}//end isLastFragment
	
	public boolean isFirstFragment(){
		return fragment_offset==0;
	}//end isLastFragment

	private int calcHeaderLength(char headLenChar) {
		String headLenStr = String.valueOf(headLenChar);
		header_length = Integer.parseInt(headLenStr, 16);
		//header_length = number of 32-bit words or 4 bytes
		return header_length * 4; //return byte length of header
	}//end calcHeaderLength
	
	public byte[] getData(){
		byte[] packetData = new byte[data_length];
		System.arraycopy(packet, IP_PAYLOAD_START, packetData, 0, data_length);
		return packetData;
	}//end getData

	public IPPacket address_filter(InetAddress source_address,
			InetAddress destination_address, InetAddress OR_source_address,
			InetAddress OR_destination_address, InetAddress AND_source_address,
			InetAddress AND_destination_address) {
		if(source_ip.equals(source_address)) return this;
		if(destination_ip.equals(destination_address)) return this;
		if(source_ip.equals(OR_source_address) || destination_ip.equals(OR_destination_address)) return this;
		if(source_ip.equals(AND_source_address) && destination_ip.equals(AND_destination_address)) return this;
		
		return null;
	}//end address_filter

	protected boolean rule_check_ipMask(IDSRule rule, String direction) {
		if(direction.equals(IDSRule.SRC2DIR)){
			return ( rule.srcCIDR.contains(this.source_ip) && rule.dstCIDR.contains(this.destination_ip) );
		}else if(direction.equals(IDSRule.BIDIRECTIONAL)){
			return ( rule.srcCIDR.contains(this.source_ip) && rule.dstCIDR.contains(this.destination_ip) )||
			( rule.dstCIDR.contains(this.source_ip) && rule.srcCIDR.contains(this.destination_ip) );
		}else{
			//not -> or <>, unrecognized
			return false;
		}//end if/else direction types
	}//rule_check_ipMask
	
	protected boolean rule_check_SAMEIP(IDSRule rule) {
		return this.source_ip.equals(this.destination_ip);
	}//end rule_check_SAMEIP
	
	protected boolean rule_check_TTL(IDSRule rule) {
		return Integer.parseInt(rule.OPTION_TTL.value)==this.time_to_live;
	}//end rule_check_TTL
	
	protected boolean rule_check_TOS(IDSRule rule) {
		return Integer.parseInt(rule.OPTION_TOS.value)==this.tos;
	}//end rule_check_TOS
	
	protected boolean rule_check_ID(IDSRule rule) {		
		return Integer.parseInt(rule.OPTION_ID.value)==this.identification;
	}//end rule_check_ID
	
	protected boolean rule_check_FRAGOFFSET(IDSRule rule) {
		return Integer.parseInt(rule.OPTION_FRAGOFFSET.value)==this.fragment_offset;
	}//end rule_check_FRAGOFFSET
	
	protected boolean rule_check_IPOPTION(IDSRule rule) {
		//temporary until code to parse out all options and set flags is written
		return this.number_options>0;
	}//end rule_check_IPOPTION
	
	protected boolean rule_check_FRAGBITS(IDSRule rule) {
		
		boolean allMod = rule.OPTION_FRAGBITS.value.contains("+");
		boolean anyMod = rule.OPTION_FRAGBITS.value.contains("*");
		boolean notMod = rule.OPTION_FRAGBITS.value.contains("!");
		boolean cR = rule.OPTION_FRAGBITS.value.contains("R");
		boolean cD = rule.OPTION_FRAGBITS.value.contains("D");
		boolean cM = rule.OPTION_FRAGBITS.value.contains("M");
		
		boolean result;
		
		if(anyMod){
			result = (cR?reserved_bit:false)||(cD?dont_fragment_bit:false)||(cM?more_fragments_bit:false);
		}else if(allMod){
			result = (cR?reserved_bit:true)&&(cD?dont_fragment_bit:true)&&(cM?more_fragments_bit:true);
		}else if(notMod){
			result = (cR?!reserved_bit:true)&&(cD?!dont_fragment_bit:true)&&(cM?!more_fragments_bit:true);
		}else{
			result = (cR?reserved_bit:!reserved_bit)&&(cD?dont_fragment_bit:!dont_fragment_bit)&&(cM?more_fragments_bit:!more_fragments_bit);
		}//end if/else mod types on fragbit check
		
		return result;
		//return rule.OPTION_FRAGBITS.isNegated() ? !result : result;
		
	}//end rule_check_FRAGBITS

	public boolean ruleCheck(IDSRule rule, IDSTuple tuple) {
		if(!rule.action.equals(IDSRule.PASS) && (rule.protocol.equals(IDSRule.IP))){
			/** IP Rule Check
			 *  If at any point it does not pass a check, return false
			 ******/
			
			//check ip/mask based on direction arrow
			if(!rule_check_ipMask(rule,rule.direction)) return false;
			
			//All packets
			//dsize
			if( rule.OPTION_DSIZE.isSet() && !rule_check_DSIZE(rule)) return false;
			//content
			if( rule.OPTION_CONTENT.isSet() && !rule_check_CONTENT(rule)) return false;
			//sameip
			if( rule.OPTION_SAMEIP.isSet() && !rule_check_SAMEIP(rule)) return false;
			//sid
			if( rule.OPTION_SID.isSet() && !rule_check_SID(rule, tuple)){
				 System.out.println("SDID false");
			return false;}
			
			/** Check options **/
			//IP and IP subclasses only
			//ttl
			if( rule.OPTION_TTL.isSet() && !rule_check_TTL(rule)) return false;
			//tos
			if( rule.OPTION_TOS.isSet() && !rule_check_TOS(rule)) return false;
			//id
			if( rule.OPTION_ID.isSet() && !rule_check_ID(rule)) return false;
			//fragoffset
			if( rule.OPTION_FRAGOFFSET.isSet() && !rule_check_FRAGOFFSET(rule)) return false;
			//ipoption
			if( rule.OPTION_IPOPTION.isSet() && !rule_check_IPOPTION(rule)) return false;
			//fragbits
			if( rule.OPTION_FRAGBITS.isSet() && !rule_check_FRAGBITS(rule)) return false;
			
			/** End IP Rule Check **/
		}else{
			System.out.println("[ERROR] Rule protocol does not match type in packet hierarchy.");
		}//end if/else protocol of rule
		
		
		//if nothing triggered a return false, it matched the rule
		//rule was matched, so return true
		return true;
	}//end ruleCheck


}//end IPPacket
