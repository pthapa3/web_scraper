// Author: Prakash Thapa
// Class:  Advance Java
// Project: Project 01
// DateDue: 09.14.2016

//package project01;


public class HeadingTag extends Tag {
    
        private int type;
        private String txtAlign;
	
        // Heading text default alignment center and type1
	public HeadingTag(){
		super();
		type = 1;
                txtAlign = "center";
	}

	public HeadingTag( String c ){
		super( c );
		type = 1;
                txtAlign = "center";
	}

	public HeadingTag( String c, int t ){
		super( c );
                txtAlign = "center";
		setType( t );
	}
        
        // Constructor for creating Heading with specific type and position
        public HeadingTag( String c, int t, String align ){
		super( c );
                txtAlign = align;
		setType( t );
	}

	public void setType( int t ){
		if( t >= 1 && t <= 6 ){
			type = t;
		}else{
			t = 1;
		}
	}
	
	public int getType(){
		return type;
	}
        
        public String getAligntype(){
		return txtAlign;
	}
        
       	//prints head text 
	public String toString(){
		return "<h" + getType() + " align = \"" + getAligntype()
                        + "\" >" + getContent() + "</h" + getType() + ">\n";
	}
	
}
   

