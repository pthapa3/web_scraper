
public class UDPPacket extends IPPacket{

	//UDP packet locations
	final int UDP_PACKET_START = IP_PAYLOAD_START;
	
	final int UDP_PORT_LEN = (int)(16/BITS_IN_BYTE);
	final int UDP_SRC_PORT = UDP_PACKET_START;
	final int UDP_DST_PORT = UDP_SRC_PORT + UDP_PORT_LEN;
	
	//variables
	public int source_port;
	public int destination_port;

	public UDPPacket(byte[] packet) {
		super(packet);
		this.packet_type = "udp";
		parse();
	}//end constructor
	
	public String toString() {
		String out = super.toString();
		
		out+=outln("Source Port: "+source_port);
		out+=outln("Destination Port: "+destination_port);
		
		return out;
	}//end toString
	
	public boolean is_type(String type){
		return ( type.equals("all") || type.equals("eth") || type.equals("ip") || type.equals("udp") );
	}//end is_type
	
	private void parse(){
		byte[] srcP = new byte[4];
		byte[] dstP = new byte[4];
		
        System.arraycopy(packet, UDP_SRC_PORT, srcP, 2, UDP_PORT_LEN);
        System.arraycopy(packet, UDP_DST_PORT, dstP, 2, UDP_PORT_LEN);
        
        source_port = bytesToInt(srcP);
        destination_port = bytesToInt(dstP);
	}//end parse
	
	public UDPPacket port_filter(Integer source_port_start,
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
	
	public boolean ruleCheck(IDSRule rule, IDSTuple tuple) {
		
		if(!rule.action.equals(IDSRule.PASS) && (rule.protocol.equals(IDSRule.IP) || rule.protocol.equals(IDSRule.UDP))){
			/** UDP Rule Check
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
			
			
			
			/** End UDP Rule Check **/
		}else{
			System.out.println("[ERROR] Rule protocol does not match type in packet hierarchy.");
		}//end if/else protocol of rule
		
		System.out.println("TRUE!!!!!!!!!!!!!");
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

}//end UDPPacket