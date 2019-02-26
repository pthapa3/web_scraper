// Author: Prakash Thapa
// Class:  Advance Java
// Project: Project 01
// DateDue: 09.14.2016

//package project01;

public class TableDataItem extends Tag{
    
            
        public TableDataItem (){
		super();
	}

        public TableDataItem ( String c ){
		super( c );
	}
                      
       
        @Override
        public String toString(){
		return "<td>" + getContent()+ "</td>";
	}
	
    
}

