// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 02
// DateDue: 09.28.2016

//package project02;

import java.util.*;

public abstract class Tag {
            
        private String content;
        private ArrayList <String> ArrayContent;
        
	public Tag(){
		content = "Default Content";
                
	}
	
	public Tag( String c ){
		content = c;
	}
        
        public  Tag( ArrayList<String> c1 ){
               ArrayContent = c1;
            
		
	}
	

	public void setContent( String c ){
		content = c;
	}
        
        
	public String getContent(){
		return content;
	}   
     	
	public abstract String toString();    
        
}
