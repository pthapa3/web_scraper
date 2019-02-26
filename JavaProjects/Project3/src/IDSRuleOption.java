
public class IDSRuleOption {
	
	String identifier;
	String value;
	boolean negate = false;

	public IDSRuleOption(String id, String val) {
		this.identifier = id;
		this.value = val;
	}//end IDSRuleOption
	
	public String toString(){
		return this.isSet() ? String.format("%s: %s\"%s\";", this.identifier,(this.negate?"!":""),this.value) : "";
	}//end toString
	
	public boolean isSet(){
		return !(this.value.isEmpty());
	}//end isSet
	
	public void setNegate() {
		this.negate = true;
	}//end setNegate
	
	public boolean isNegated() {
		return this.negate;
	}//end isNegated
	
	public void set(String val, boolean negated){
		this.value = val;
		this.negate = negated;
	}//end set
	
	/** Testing **/
	public static void main(String[] args) {
		IDSRuleOption msgOption = new IDSRuleOption("msg","");
		System.out.println(msgOption.isSet());
		System.out.println(msgOption);
		
		msgOption.set("print this message",false);
		System.out.println(msgOption.isSet());
		System.out.println(msgOption);
		System.out.println(msgOption.value);
		
		msgOption.set("print this message",true);
		System.out.println(msgOption.isSet());
		System.out.println(msgOption);
		System.out.println(msgOption.value);
		
		System.out.println("testing string contains() ");
		System.out.println("!MD+*".contains("-"));
		
		
	}//end main

}//end IDSRuleOption
