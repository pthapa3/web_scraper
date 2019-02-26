// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 02
// DateDue: 09.28.2016

//package project02;

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
