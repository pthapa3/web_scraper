// Author: Prakash Thapa
// Class:  Advance Java
// Project: Project 01
// DateDue: 09.14.2016

//package project01;

import java.util.*;


public class TableRowTag extends Tag{
    
    private ArrayList<Tag> tHeadTextList;
    
    public TableRowTag(){
        super();
        tHeadTextList = new ArrayList <Tag> ();
        
    }
    public void appendTabRow(Tag t){
        
        tHeadTextList.add(t);
        
    }
     
    @Override
    public String toString(){
        
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        for(Tag t : tHeadTextList){
             sb.append(t);
             sb.append ("\n");
        }
        sb.append ("</tr>\n");
        
        return  sb.toString() ;
        
        
        
    }
    
    
}
