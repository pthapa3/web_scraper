// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 02
// DateDue: 09.28.2016

//package project02;

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

