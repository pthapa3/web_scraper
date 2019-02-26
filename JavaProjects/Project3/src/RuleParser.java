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
public class RuleParser {
	final String name = "noah-rule-parser";
	final String version = "0.0.1a";
	
	private String input_filename;
	
	/** Separate rules by protocol  **/
	Vector<IDSRule> ALL_RULES = new Vector<IDSRule>();
	Vector<IDSRule> IP_RULES = new Vector<IDSRule>();
	Vector<IDSRule> ARP_RULES = new Vector<IDSRule>();
	Vector<IDSRule> TCP_RULES = new Vector<IDSRule>();
	Vector<IDSRule> UDP_RULES = new Vector<IDSRule>();
	Vector<IDSRule> ICMP_RULES = new Vector<IDSRule>();

	public RuleParser(String filename) {
		System.out.println(this.name+" "+this.version);
		this.input_filename = filename;
		
		//Parse rules
		this.init();
		this.printRules();
	}//end constructor

	/**
	 * Parse the passed arguments in a swtich statement in while
	 * @param args
	 */
	public static void main(String[] args) {
		String filename;
		RuleParser ruleParser;
		if (args.length > 0){
			//Create rule parser instance
			ruleParser = new RuleParser(args[0]);
		}//end if arguments
		
	}//end main
	
	private void printRules(){
		for (Iterator iterator = ALL_RULES.iterator(); iterator.hasNext();) {
			IDSRule rule = (IDSRule) iterator.next();
			System.out.println(rule);
		}//end for each rule in rules
	}//end printRules

	private void init() {
		//read rule line by line
		
		FileReader fr;
		BufferedReader br;
		try {
			fr = new FileReader(input_filename);
			br = new BufferedReader(fr);
			
			String line;
			IDSRule rule;
			while((line = br.readLine()) != null) {
				//Only parse non-empty lines to end of file
				if(!line.isEmpty()){
					String[] theline = line.split("\\(");
					
					//Rule Header
					String headStr = theline[0];
					String[] header = headStr.split(" ");
					rule = new IDSRule(header[0], header[1]);
					
					rule.parseSrcAddr(header[2]);
					rule.parseSrcPort(header[3]);
					rule.parseDirection(header[4]);
					rule.parseDstAddr(header[5]);
					rule.parseDstPort(header[6]);
					
					//Rule Options
					if(theline.length > 1){ //if there are options, parse them
						rule.parseOptions(theline[1].substring(0, theline[1].length()-1)); //remove trailing ')'
					}//end if options
					
					//Finally add parsed rule to rules vector
					addRule(rule);
					
				}//end if empty line
				
			}//end while
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Invalid input file.");
		}//bad file
		
	}//end init

	private void addRule(IDSRule rule) {
		ALL_RULES.add(rule);
		if(rule.protocol.equals(IDSRule.IP)){
			IP_RULES.add(rule);		
			TCP_RULES.add(rule);			
			UDP_RULES.add(rule);
			ICMP_RULES.add(rule);
		}else if(rule.protocol.equals(IDSRule.ARP)){
			ARP_RULES.add(rule);
		}else if(rule.protocol.equals(IDSRule.TCP)){
			TCP_RULES.add(rule);
		}else if(rule.protocol.equals(IDSRule.UDP)){
			UDP_RULES.add(rule);
		}else if(rule.protocol.equals(IDSRule.ICMP)){
			ICMP_RULES.add(rule);
		}//end if/else protocols
	}//end addRule

}//end PacketParser
