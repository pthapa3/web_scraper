
public class ICMPPacket extends IPPacket{

	//ICMP packet locations
	final int ICMP_PACKET_START = IP_PAYLOAD_START;
	
	final int ICMP_TYPE_LEN = (int)(8/BITS_IN_BYTE);
	final int ICMP_CODE_LEN = (int)(8/BITS_IN_BYTE);
	final int ICMP_CHECKSUM_LEN = (int)(16/BITS_IN_BYTE);
	
	final int ICMP_TYPE = ICMP_PACKET_START;
	final int ICMP_CODE = ICMP_TYPE+ICMP_TYPE_LEN;
	final int ICMP_CHECKSUM = ICMP_CODE+ICMP_CODE_LEN;
	
	//variables
	public int icmp_type;
	public int icmp_code;
	public int icmp_checksum;

	public ICMPPacket(byte[] packet) {
		super(packet);
		this.packet_type = "icmp";
		parse();
	}//end constructor
	
	public String toString() {
		String out = super.toString();
		
		out+=outln("Type: "+icmp_type);
		out+=outln("Code: "+icmp_code);
		
		return out;
	}//end toString
	
	public boolean is_type(String type){
		return ( type.equals("all") || type.equals("eth") || type.equals("ip") || type.equals("icmp") );
	}//end is_type
	
	private void parse(){
		byte[] type = new byte[1];
		byte[] code = new byte[1];
		
        System.arraycopy(packet, ICMP_TYPE, type, 0, ICMP_TYPE_LEN);
        System.arraycopy(packet, ICMP_CODE, code, 0, ICMP_CODE_LEN);
        
        icmp_type = unsignedByteToInt(type[0]);
        icmp_code = unsignedByteToInt(code[0]);
	}//end parse
	
	public boolean ruleCheck(IDSRule rule, IDSTuple tuple) {
		
		if(!rule.action.equals(IDSRule.PASS) && (rule.protocol.equals(IDSRule.IP) || rule.protocol.equals(IDSRule.ICMP))){
			/** ICMP Rule Check
			 *  If at any point it does not pass a check, return false
			 ******/
			
			//check ip/mask based on direction arrow
			if(!rule_check_ipMask(rule,rule.direction)) return false;
			//check port (no ports in icmp..)
			//if(!rule_check_ports(rule,rule.direction)) return false;
			
			
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
			
			//itype
			if( rule.OPTION_ITYPE.isSet() && !rule_check_ITYPE(rule)) return false;
			//icode
			if( rule.OPTION_ICODE.isSet() && !rule_check_ICODE(rule)) return false;
			
			/** End ICMP Rule Check **/
		}else{
			System.out.println("[ERROR] Rule protocol does not match type in packet hierarchy.");
		}//end if/else protocol of rule
		
		
		//if nothing triggered a return false, it matched the rule
		//rule was matched, so return true
		return true;
		
	}//end ruleCheck
	
	protected boolean rule_check_ITYPE(IDSRule rule){
		return Integer.parseInt(rule.OPTION_ITYPE.value)==this.icmp_type;
	}//end rule_check_ITYPE
	
	protected boolean rule_check_ICODE(IDSRule rule){
		return Integer.parseInt(rule.OPTION_ICODE.value)==this.icmp_code;
	}//end rule_check_ICODE

}//end ICMPPacket