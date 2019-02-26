// Author: Prakash Thapa
// Class:  Advance Java
// Project: Project 01
// DateDue: 09.14.2016

//package project01;

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
