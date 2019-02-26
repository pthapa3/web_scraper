import java.net.InetAddress;
import java.net.UnknownHostException;


public class IDSRule {
	
	/** Actions **/
	static final String ALERT = "alert";
	static final String PASS = "pass";
	
	/** Protocols **/
	static final String IP = "ip";
	static final String ARP = "arp";
	static final String TCP = "tcp";
	static final String UDP = "udp";
	static final String ICMP = "icmp";
	
	private static final String ANY = "any";
	
	/** Directions **/
	static final String BIDIRECTIONAL = "<>";
	static final String SRC2DIR = "->";
	
	/** Options **/
	IDSRuleOption OPTION_MSG = new IDSRuleOption("msg", "");
	IDSRuleOption OPTION_LOGTO = new IDSRuleOption("logto", "");
	IDSRuleOption OPTION_TTL = new IDSRuleOption("ttl", "");
	IDSRuleOption OPTION_TOS = new IDSRuleOption("tos", "");
	IDSRuleOption OPTION_ID = new IDSRuleOption("id", "");
	IDSRuleOption OPTION_FRAGOFFSET = new IDSRuleOption("fragoffset", "");
	IDSRuleOption OPTION_IPOPTION = new IDSRuleOption("ipoption", "");
	IDSRuleOption OPTION_FRAGBITS = new IDSRuleOption("fragbits", "");
	IDSRuleOption OPTION_DSIZE = new IDSRuleOption("dsize", "");
	IDSRuleOption OPTION_FLAGS = new IDSRuleOption("flags", "");
	IDSRuleOption OPTION_SEQ = new IDSRuleOption("seq", "");
	IDSRuleOption OPTION_ACK = new IDSRuleOption("ack", "");
	IDSRuleOption OPTION_ITYPE = new IDSRuleOption("itype", "");
	IDSRuleOption OPTION_ICODE = new IDSRuleOption("icode", "");
	IDSRuleOption OPTION_CONTENT = new IDSRuleOption("content", "");
	IDSRuleOption OPTION_SAMEIP = new IDSRuleOption("sameip", "");
	IDSRuleOption OPTION_SID = new IDSRuleOption("sid", "");
	
	String action;
	String protocol;
	CIDR srcCIDR;
	PortRange srcPort;
	String direction;
	CIDR dstCIDR;
	PortRange dstPort;
	String options;

	public IDSRule(String action, String protocol) {
		if(action.equals(ALERT)||action.equals(PASS)){
			this.action = action;
		}else{
			throw new IllegalArgumentException(String.format("invalid action value: %s must be [alert|pass]", action));
		}//end if/else valid action
		
		if(protocol.equals(IP)||protocol.equals(ARP)||protocol.equals(TCP)||protocol.equals(UDP)||protocol.equals(ICMP)){
			this.protocol = protocol;
		}else{
			throw new IllegalArgumentException(String.format("invalid protocol value: %s must be [ip|arp|tcp|udp|icmp]", protocol));
		}//end if/else valid protocol
	}//end constructor
	
	public String toString(){
		return String.format("%s %s %s %s %s %s %s (%s)",action,protocol,srcCIDR,srcPort,direction,dstCIDR,dstPort,this.reconstructOptions());
	}//end toString
	
	public String alert(){
		return this.toString();
	}//end alert
	
	public String log(){
		return OPTION_MSG.isSet() ? String.format("%s (%s)",OPTION_MSG.value,this.alert()) : this.alert();
	}//end log

	public void parseSrcAddr(String string){
		if(string.equals(ANY)){
			srcCIDR = new CIDR(true); //null means don't use this to filter
		}else if(string.indexOf('/')!=-1){//else if CIDR notation, use CIDR(string)
			try{
				srcCIDR = new CIDR(string);
			}catch (Exception e) {e.printStackTrace();}//end try/catch
		}else{//no mask specified use CIDR(inet, mask=32)
			try{
				srcCIDR = new CIDR(InetAddress.getByName(string), 32);
			}catch (Exception e) {e.printStackTrace();}//end try/catch
		}//end if/else source address types
	}//end parseSrcAddr

	public void parseSrcPort(String string) {
		this.srcPort = new PortRange(string);
	}//end parseSrcPort

	public void parseDirection(String string) {
		this.direction = string;
	}//end parseDirection

	public void parseDstAddr(String string) {
		if(string.equals(ANY)){
			dstCIDR = new CIDR(true);; //null means don't use this to filter
		}else if(string.indexOf('/')!=-1){//else if CIDR notation, use CIDR(string)
			try{
				dstCIDR = new CIDR(string);
			}catch (Exception e) {e.printStackTrace();}//end try/catch
		}else{//no mask specified use CIDR(inet, mask=32)
			try{
				dstCIDR = new CIDR(InetAddress.getByName(string), 32);
			}catch (Exception e) {e.printStackTrace();}//end try/catch
		}//end if/else destination address types
	}//end parseDstAddr

	public void parseDstPort(String string) {
		this.dstPort = new PortRange(string);
	}//end parseDstPort

	public void parseOptions(String optionsString) {
		this.options = optionsString;
		
		String[] options = optionsString.split(";");
		
		String option;
		String optID;
		String optVal;
		boolean negated;
		for (int i = 0; i < options.length; i++) {
			option = options[i].trim();
			optID = option.split(":")[0].trim();
			optVal = option.split(":")[1].trim().replaceAll("\"","");
			negated = optVal.startsWith("!");
			//remove the !
			optVal = optVal.replaceAll("!","");
			
			//System.out.println('$'+optID+'$');
			//System.out.println('$'+optVal+'$');
			
			switch(optID.charAt(0)){
				case 'm':
					OPTION_MSG.set(optVal, negated);
					break;
				case 'l':
					OPTION_LOGTO.set(optVal, negated);
					break;
				case 't':
					if(optID.equals(OPTION_TTL.identifier)){
						OPTION_TTL.set(optVal, negated);	
					}else if(optID.equals(OPTION_TOS.identifier)){
						OPTION_TOS.set(optVal, negated);
					}
					break;
				case 'i':
					if(optID.equals(OPTION_ID.identifier)){
						OPTION_ID.set(optVal, negated);
					}else if(optID.equals(OPTION_IPOPTION.identifier)){
						OPTION_IPOPTION.set(optVal, negated);
					}else if(optID.equals(OPTION_ITYPE.identifier)){
						OPTION_ITYPE.set(optVal, negated);
					}else if(optID.equals(OPTION_ICODE.identifier)){
						OPTION_ICODE.set(optVal, negated);
					}
					break;
				case 'f':
					if(optID.equals(OPTION_FRAGOFFSET.identifier)){
						OPTION_FRAGOFFSET.set(optVal, negated);	
					}else if(optID.equals(OPTION_FRAGBITS.identifier)){
						OPTION_FRAGBITS.set(optVal, negated);
					}else if(optID.equals(OPTION_FLAGS.identifier)){
						OPTION_FLAGS.set(optVal, negated);
					}
					break;
				case 'd':
					OPTION_DSIZE.set(optVal, negated);
					break;
				case 's':
					if(optID.equals(OPTION_SEQ.identifier)){
						OPTION_SEQ.set(optVal, negated);	
					}else if(optID.equals(OPTION_SAMEIP.identifier)){
						OPTION_SAMEIP.set(optVal, negated);
					}else if(optID.equals(OPTION_SID.identifier)){
						OPTION_SID.set(optVal, negated);
					}
					break;
				case 'a':
					OPTION_ACK.set(optVal, negated);
					break;
				case 'c':
					OPTION_CONTENT.set(optVal, negated);
					break;
				default: 
					//usage();
					System.out.println("Invalid option.");
					break;
			}//end switch
			
		}//end for
		
	}//end parseOptions
	
	private String reconstructOptions(){
		//Verify the options parsed correctly
		return String.format("%s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s ",
				OPTION_MSG,OPTION_LOGTO,OPTION_TTL,OPTION_TOS,OPTION_ID,
				OPTION_FRAGOFFSET,OPTION_IPOPTION,OPTION_FRAGBITS,OPTION_DSIZE,
				OPTION_FLAGS,OPTION_SEQ,OPTION_ACK,OPTION_ITYPE,OPTION_ICODE,
				OPTION_CONTENT,OPTION_SAMEIP,OPTION_SID).replaceAll("[ \t\n\r\f]{2,}"," ").trim();
	}//end reconstructOptions

}//end IDSRule
