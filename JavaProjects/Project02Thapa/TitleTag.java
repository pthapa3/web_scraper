// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 02
// DateDue: 09.28.2016

//package project02;


public class TitleTag extends Tag{
   
   
    public TitleTag(){
	   super();
    }
	
    public TitleTag( String t ){
           super( t );
          
    }    
   
    public String toString(){
	   return "<title>" + getContent() + "</title>\n";
    }
}