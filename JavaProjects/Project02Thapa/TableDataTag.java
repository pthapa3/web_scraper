// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 02
// DateDue: 09.28.2016

//package project02;

import java.util.*;


public class TableDataTag extends Tag{
   
    
    private ArrayList<Tag> tDataTextList;
    
    public TableDataTag(){
        super();
        tDataTextList = new ArrayList <Tag> ();
       
    }   
  
    // Appends table data
    public void appendTabData(Tag t){
        
        tDataTextList.add(t);
        
    }
     
    @Override
    public String toString(){
        
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>\n");
        for(Tag t : tDataTextList){
             sb.append(t);
             sb.append ("\n");
        }
        sb.append ("</tr>\n");
        
        return  sb.toString();     
    }
    
}


