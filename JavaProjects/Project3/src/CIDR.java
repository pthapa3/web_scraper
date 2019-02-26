import java.net.InetAddress;
import java.net.UnknownHostException;


public class CIDR {
	
	InetAddress baseAddress;
	private int addressInt;
	private int cidrMask;
	private int addressEndInt;
	
	private boolean ANY = false; 
	
	/**
	 * Constructors
	 * @throws UnknownHostException 
	 * @throws NumberFormatException 
	 */
	public CIDR(String CIDRNotation) throws NumberFormatException, UnknownHostException{
		/*
		String[] addrAndMask = CIDRNotation.split("/");
		InetAddress address = InetAddress.getByName(addrAndMask[0]);
		int mask = Integer.parseInt(addrAndMask[1]);
		this(address, mask);
		*/
		this(InetAddress.getByName(CIDRNotation.split("/")[0]), Integer.parseInt(CIDRNotation.split("/")[1]));
	}//end constructor for CIDR notation
	public CIDR(InetAddress newaddr, int mask) {
		cidrMask = mask;
		addressInt = ipv4AddressToInt(newaddr);
		int newmask = ipv4PrefixLengthToMask(mask);
		addressInt &= newmask;
		try{
			baseAddress = intToIPv4Address(addressInt);
		}catch(UnknownHostException e){
			//this should never happen
		}//end try/catch
		addressEndInt = addressInt + ipv4PrefixLengthToLength(cidrMask) - 1;
	}//end constructor for inet address and int mask
	
	public CIDR(boolean any) {
		this.ANY = any;
	}//end ANY constructor
	
	/**
	 * Public Methods
	 */
	public String toString(){
		if(this.ANY) return "any"; //return "any" if any
		
		try {
			return String.format("%s/%s", this.getFirstAddress(), cidrMask);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return "Invalid CIDR notation.";
		}//end try/catch
	}//end toString
	
	public boolean contains(InetAddress inetAddress) {
		if(this.ANY) return true; //return true if 'any'
		
		int search = ipv4AddressToInt(inetAddress);
		return search >= addressInt && search <= addressEndInt;
	}//end contains
	
	public InetAddress getFirstAddress() throws UnknownHostException{
		return intToIPv4Address(this.addressInt);	
	}//end getBaseAddress
	
	public InetAddress getLastAddress() throws UnknownHostException{
		return intToIPv4Address(this.addressEndInt);	
	}//end getBaseAddress
	
	
	
	/**
	 * Auxiliary Methods
	 */
	/** Given an IPv4 baseAddress length, return the block length.  i.e., a
	 * baseAddress length of 24 will return 256. 
	 */
	private static int ipv4PrefixLengthToLength(int prefix_length) {
		return 1 << 32 - prefix_length;
	}//end ipv4PrefixLengthToLength
	/** Given a baseAddress length, return a netmask.  I.e, a baseAddress length
	 * of 24 will return 0xFFFFFF00. 
	 */
	private static int ipv4PrefixLengthToMask(int prefix_length) {
		return ~((1 << 32 - prefix_length) - 1);
	}//end ipv4PrefixLengthToMask
	private static InetAddress intToIPv4Address(int addr) throws UnknownHostException{
		byte[] a = new byte[4];
		a[0] = (byte) (addr >> 24 & 0xFF);
		a[1] = (byte) (addr >> 16 & 0xFF);
		a[2] = (byte) (addr >> 8 & 0xFF);
		a[3] = (byte) (addr & 0xFF);
		return InetAddress.getByAddress(a);
	}//end intToIPv4Address
	private static int ipv4AddressToInt(InetAddress addr) {
		byte[] address = null;
		address = addr.getAddress();
		return ipv4AddressToInt(address);
	}//end ipv4AddressToInt(InetAddr)
	private static int ipv4AddressToInt(byte[] address) {
		int net = 0;
		for(byte addres: address){
			net <<= 8;
			net |= addres & 0xFF;
		}//end for
		return net;
	}//end ipv4AddressToInt(byte[])
	
	
	
	
	/**
	 * Tests
	 * @param args
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException {
		//InetAddress inetAddr = InetAddress.getByName("129.244.0.0");
		//int mask = 20;
		//CIDR cidr = new CIDR(inetAddr, mask);
		
		/*
		CIDR cidr = new CIDR("129.244.0.0/20");
		System.out.println(cidr);
		System.out.println(cidr.getFirstAddress());
		System.out.println(cidr.getLastAddress());
		
		System.out.println(cidr.contains(InetAddress.getByName("129.244.15.243")));
		System.out.println(cidr.contains(InetAddress.getByName("129.244.10.56")));
		System.out.println(cidr.contains(InetAddress.getByName("129.244.16.134")));
		*/
		
		/*
		CIDR cidr = new CIDR("129.244.254.150/32");
		System.out.println(cidr);
		System.out.println(cidr.getFirstAddress());
		System.out.println(cidr.getLastAddress());
		
		System.out.println(cidr.contains(InetAddress.getByName("129.244.254.150")));
		System.out.println(cidr.contains(InetAddress.getByName("129.244.15.243")));
		System.out.println(cidr.contains(InetAddress.getByName("129.244.10.56")));
		System.out.println(cidr.contains(InetAddress.getByName("129.244.16.134")));
		*/
		
		//ANY
		CIDR cidr = new CIDR(true);
		System.out.println(cidr.contains(InetAddress.getByName("129.244.254.150")));
		System.out.println(cidr.contains(InetAddress.getByName("129.244.15.243")));
		System.out.println(cidr.contains(InetAddress.getByName("129.244.10.56")));
		System.out.println(cidr.contains(InetAddress.getByName("129.244.16.134")));
		
	}//end main

}//end CIDR
