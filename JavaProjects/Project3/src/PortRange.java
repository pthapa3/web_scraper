import java.net.InetAddress;
import java.net.UnknownHostException;


public class PortRange {
	
	private static final String ANY = "any";
	
	private int startPort;
	private int endPort;
	private boolean any = false;
	
	/**
	 * Constructors 
	 */
	public PortRange(String ports){
		//verifyPort(56);
		if(ports.equals(ANY)){
			this.startPort = 0;
			this.endPort = 65535;
			this.any = true;
		}else if(ports.indexOf(':')!=-1){
			if(ports.indexOf(':')==0){// :port2
				this.startPort = 0;
				this.endPort = verifyPort(Integer.parseInt(ports.split(":")[1]));
			}else{// port1:port2
				this.startPort = verifyPort(Integer.parseInt(ports.split(":")[0]));
				this.endPort = verifyPort(Integer.parseInt(ports.split(":")[1]));
			}//end range ports
		}else{//port specified, not range (single port)
			this.startPort = verifyPort(Integer.parseInt(ports));
			this.endPort = verifyPort(Integer.parseInt(ports));
		}//end if/else port styles
		
	}//end constructor for CIDR notation
	
	/**
	 * Public Methods
	 */
	public String toString(){
		if(this.any) return "any"; //return "any" if any
		
		return String.format("%s:%s", this.getStartPort(), this.getEndPort());
	}//end toString
	
	public boolean contains(int port) {
		return port >= startPort && port <= endPort;
	}//end contains
	
	public int getStartPort(){
		return this.startPort;
	}//end getStartPort
	
	public int getEndPort(){
		return this.endPort;
	}//end getEndPort
	
	/**
	 * Auxiliary Methods
	 * @return 
	 */
	private int verifyPort(int port){
		return (port >= 0 && port <= 65535) ? port : -1;
	}//end verifyPort
	
	
	/**
	 * Tests
	 * @param args 
	 */
	public static void main(String[] args){
		PortRange pr = new PortRange("1:65535");
		System.out.println(pr);
		System.out.println(pr.contains(80));
		System.out.println(pr.contains(-5));
		System.out.println(pr.contains(000));
		System.out.println(pr.contains(75000));
		
		PortRange pr2 = new PortRange("-1:234535");
		System.out.println(pr2);
		System.out.println(pr2.contains(80));
		System.out.println(pr2.contains(-5));
		System.out.println(pr2.contains(000));
		System.out.println(pr2.contains(75000));
	}//end main

}//end DatagramBuffer
