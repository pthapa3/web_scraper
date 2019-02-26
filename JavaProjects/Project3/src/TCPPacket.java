
public class TCPPacket extends IPPacket{
	
	//tcp packet locations
	final int TCP_PACKET_START = IP_PAYLOAD_START;
	
	final static int TCP_PORT_LEN = (int)(16/BITS_IN_BYTE);
	final int TCP_SRC_PORT = TCP_PACKET_START;
	final int TCP_DST_PORT = TCP_SRC_PORT + TCP_PORT_LEN;
	final static int TCP_SEQ_LEN = (int)(32/BITS_IN_BYTE);
	final int TCP_SEQ = TCP_DST_PORT + TCP_PORT_LEN;
	final static int TCP_ACK_LEN = (int)(32/BITS_IN_BYTE);
	final int TCP_ACK = TCP_SEQ + TCP_SEQ_LEN;
	final static int TCP_FLAGS_LEN = (int)(8/BITS_IN_BYTE);
	final int TCP_FLAGS = TCP_ACK + TCP_ACK_LEN + (int)(8/BITS_IN_BYTE); // +1 byte to skip over 'data offset' and 'reserved' fields
	
	//variables
	public int source_port;
	public int destination_port;
	public int sequence_number;
	public int acknowlegement_number;
	public String flag_bits;
	/**
	 * Control bits (from left to right, i.e. 0th to 7th bit)
	 * CWR (1 bit) – Congestion Window Reduced (CWR) flag is set by the sending host to indicate that it received a TCP segment with the ECE flag set and had responded in congestion control mechanism (added to header by RFC 3168).
	 * ECE (1 bit) – ECN-Echo indicates
		 * If the SYN flag is set (1), that the TCP peer is ECN capable.
		 * If the SYN flag is clear (0), that a packet with Congestion Experienced flag in IP header set is received during normal transmission (added to header by RFC 3168).
	 * URG (1 bit) – indicates that the Urgent pointer field is significant
	 * ACK (1 bit) – indicates that the Acknowledgment field is significant. All packets after the initial SYN packet sent by the client should have this flag set.
	 * PSH (1 bit) – Push function. Asks to push the buffered data to the receiving application.
	 * RST (1 bit) – Reset the connection
	 * SYN (1 bit) – Synchronize sequence numbers. Only the first packet sent from each end should have this flag set. Some other flags change meaning based on this flag, and some are only valid for when it is set, and others when it is clear.
	 * FIN (1 bit) – No more data from sender
	 *****/
	public boolean cwr_flag;
	public boolean ece_flag;
	public boolean urg_flag;
	public boolean ack_flag;
	public boolean psh_flag;
	public boolean rst_flag;
	public boolean syn_flag;
	public boolean fin_flag;

	public TCPPacket(byte[] packet) {
		super(packet);
		this.packet_type = "tcp";
		parse();
	}//end constructor
	
	public String toString() {
		String out = super.toString();
		
		out+=outln("Source Port: "+source_port);
		out+=outln("Destination Port: "+destination_port);
		out+=outln("Sequence Number: "+sequence_number);
		out+=outln("Acknowledgement Number: "+acknowlegement_number);
		out+=outln("Control Flags: "+flag_bits);
		out+=outln("URG Flag: "+urg_flag);
		
		return out;
	}//end toString
	
	public boolean is_type(String type){
		return ( type.equals("all") || type.equals("eth") || type.equals("ip") || type.equals("tcp") );
	}//end is_type
	
	private void parse(){
		byte[] srcP = new byte[TCP_PORT_LEN];
		byte[] dstP = new byte[TCP_PORT_LEN];
		byte[] seq = new byte[TCP_SEQ_LEN];
		byte[] ack = new byte[TCP_ACK_LEN];
		byte[] flags = new byte[TCP_FLAGS_LEN];
		
        System.arraycopy(packet, TCP_SRC_PORT, srcP, 0, TCP_PORT_LEN);
        System.arraycopy(packet, TCP_DST_PORT, dstP, 0, TCP_PORT_LEN);
        System.arraycopy(packet, TCP_SEQ, seq, 0, TCP_SEQ_LEN);
        System.arraycopy(packet, TCP_ACK, ack, 0, TCP_ACK_LEN);
        System.arraycopy(packet, TCP_FLAGS, flags, 0, TCP_FLAGS_LEN);
        
        source_port = bytesToDecimal(srcP);
        destination_port = bytesToDecimal(dstP);
        sequence_number = bytesToDecimal(seq);
        acknowlegement_number = bytesToDecimal(ack);
        parseFlags(flags);
        
	}//end parse

	public TCPPacket port_filter(Integer source_port_start,
			Integer source_port_end, Integer destination_port_start,
			Integer destination_port_end) {
		if(source_port_start!=null && source_port_end!=null){
			if(source_port_start <= source_port && source_port <= source_port_end) return this;
		}
		if(destination_port_start!=null && destination_port_end!=null){
			if(source_port_start <= destination_port && destination_port <= source_port_end) return this;
		}
		return null;
	}//end port_filter
	
	private void parseFlags(byte[] flagsBytes) {
		String flags = bytesToBinary(flagsBytes);
		flag_bits = flags;
		
		cwr_flag = flags.charAt(0)=='1';
		ece_flag = flags.charAt(1)=='1';
		urg_flag = flags.charAt(2)=='1';
		ack_flag = flags.charAt(3)=='1';
		psh_flag = flags.charAt(4)=='1';
		rst_flag = flags.charAt(5)=='1';
		syn_flag = flags.charAt(6)=='1';
		fin_flag = flags.charAt(7)=='1';
	}//end parseFlagFrag

	public boolean ruleCheck(IDSRule rule, IDSTuple tuple) {
		
		if(!rule.action.equals(IDSRule.PASS) && (rule.protocol.equals(IDSRule.IP) || rule.protocol.equals(IDSRule.TCP))){
			/** TCP Rule Check
			 *  If at any point it does not pass a check, return false
			 ******/
			
			//check ip/mask based on direction arrow
			if(!rule_check_ipMask(rule,rule.direction)) return false;
			//check port
			if(!rule_check_ports(rule,rule.direction)) return false;
			
			
			//All packets
			//dsize
			if( rule.OPTION_DSIZE.isSet() && !rule_check_DSIZE(rule)) return false;
			//content
			if( rule.OPTION_CONTENT.isSet() && !rule_check_CONTENT(rule)) return false;
			//sameip
			if( rule.OPTION_SAMEIP.isSet() && !rule_check_SAMEIP(rule)) return false;
			//sid
			if( rule.OPTION_SID.isSet() && !rule_check_SID(rule, tuple)) return false;
			
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
			
			//TCP only
			//flags
			if( rule.OPTION_FLAGS.isSet() && !rule_check_FLAGS(rule)) return false;
			//seq
			if( rule.OPTION_SEQ.isSet() && !rule_check_SEQ(rule)) return false;
			//ack
			if( rule.OPTION_ACK.isSet() && !rule_check_ACK(rule)) return false;
			
			/*//ICMP Only
			//itype
			if( rule.OPTION_ITYPE.isSet() && !rule_check_ITYPE(rule)) return false;
			//icode
			if( rule.OPTION_ICODE.isSet() && !rule_check_ICODE(rule)) return false;
			*/
			
			/** End TCP Rule Check **/
		}else{
			System.out.println("[ERROR] Rule protocol does not match type in packet hierarchy.");
		}//end if/else protocol of rule
		
		
		//if nothing triggered a return false, it matched the rule
		//rule was matched, so return true
		return true;
		
	}//end ruleCheck

	private boolean rule_check_ports(IDSRule rule, String direction) {
		if(direction.equals(IDSRule.SRC2DIR)){
			return ( rule.srcPort.contains(this.source_port) && rule.dstPort.contains(this.destination_port) );
		}else if(direction.equals(IDSRule.BIDIRECTIONAL)){
			return ( rule.srcPort.contains(this.source_port) && rule.dstPort.contains(this.destination_port) )||
			( rule.dstPort.contains(this.source_port) && rule.srcPort.contains(this.destination_port) );
		}else{
			//not -> or <>, unrecognized
			return false;
		}//end if/else direction types
	}//end rule_check_ports

	private boolean rule_check_FLAGS(IDSRule rule) {
		boolean allMod = rule.OPTION_FLAGS.value.contains("+");
		boolean anyMod = rule.OPTION_FLAGS.value.contains("*");
		boolean notMod = rule.OPTION_FLAGS.value.contains("!");
		boolean cF = rule.OPTION_FLAGS.value.contains("F");
		boolean cS = rule.OPTION_FLAGS.value.contains("S");
		boolean cR = rule.OPTION_FLAGS.value.contains("R");
		boolean cP = rule.OPTION_FLAGS.value.contains("P");
		boolean cA = rule.OPTION_FLAGS.value.contains("A");
		boolean cU = rule.OPTION_FLAGS.value.contains("U");
		boolean c1 = rule.OPTION_FLAGS.value.contains("1");
		boolean c2 = rule.OPTION_FLAGS.value.contains("2");
		boolean c0 = rule.OPTION_FLAGS.value.contains("0");
		
		/*
		cwr_flag //1
		ece_flag //2
		urg_flag //U
		ack_flag //A
		psh_flag //P
		rst_flag //R
		syn_flag //S
		fin_flag //F
		*/
		boolean result;
		if(anyMod){
			result = (cF?fin_flag:false)||(cS?syn_flag:false)||(cR?rst_flag:false)||
			(cP?psh_flag:false)||(cA?ack_flag:false)||(cU?urg_flag:false)||
			(c2?ece_flag:false)||(c1?cwr_flag:false);
		}else if(allMod){
			result = (cF?fin_flag:true)&&(cS?syn_flag:true)&&(cR?rst_flag:true)&&
			(cP?psh_flag:true)&&(cA?ack_flag:true)&&(cU?urg_flag:true)&&
			(c2?ece_flag:true)&&(c1?cwr_flag:true);
		}else if(notMod){
			result = (cF?!fin_flag:true)&&(cS?!syn_flag:true)&&(cR?!rst_flag:true)&&
			(cP?!psh_flag:true)&&(cA?!ack_flag:true)&&(cU?!urg_flag:true)&&
			(c2?!ece_flag:true)&&(c1?!cwr_flag:true);
		}else{
			result = (cF?fin_flag:!fin_flag)&&(cS?syn_flag:!syn_flag)&&(cR?rst_flag:!rst_flag)&&
			(cP?psh_flag:!psh_flag)&&(cA?ack_flag:!ack_flag)&&(cU?urg_flag:!urg_flag)&&
			(c2?ece_flag:!ece_flag)&&(c1?cwr_flag:!cwr_flag);
		}//end if/else mod types on fragbit check
		
		return result;
	}//end rule_check_FLAGS
	
	private boolean rule_check_SEQ(IDSRule rule) {
		return Integer.parseInt(rule.OPTION_SEQ.value)==this.sequence_number;
	}//end rule_check_SEQ
	
	private boolean rule_check_ACK(IDSRule rule) {
		return Integer.parseInt(rule.OPTION_ACK.value)==this.acknowlegement_number;
	}//end rule_check_ACK

}//end TCPPacket