// Author: Prakash Thapa
// Class:  Advance Java
// Project: Project 01
// DateDue: 09.14.2016

//package project01;

// class for Table Head Text
public class TableHeadItems extends Tag {
    
       
        public TableHeadItems (){
		super();
	}
  
              
        public  TableHeadItems ( String  c ){
            super(c);
               
        }            
            
        @Override
        public String toString(){
		return "<th>" + getContent()+ "</th>";
	}
	
    
}
