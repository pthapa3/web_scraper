// Author: Prakash Thapa
// Class:  Advance Java
// Project: Project 01
// DateDue: 09.14.2016

//package project01;


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