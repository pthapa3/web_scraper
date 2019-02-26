// Author: Prakash Thapa
// Class:  Advance Java
// Project: Project 01
// DateDue: 09.14.2016

//package project01;

public class AnchorTag extends Tag{

	private String URL;
	private String DesCripText;
        
        //For Description of the link, url, and linktext
	public AnchorTag( String text, String c, String u ){
		super( c );
                DesCripText = text;
		setURL( u );
                
	}

	public void setURL( String u ){
	       URL = u;
	}
	
	public String getURL(){
	       return URL;
	}
	
        public String getText(){
            return DesCripText;
            
        }
	public String toString(){
	      return "<p>" + getText() + "\n" + "<a href=\"" + getURL() + "\">" 
                      + getContent() + "</a></p>\n";
	}
	
}
