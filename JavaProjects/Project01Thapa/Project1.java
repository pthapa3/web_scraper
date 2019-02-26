// Author: Prakash Thapa
// Class:  Advance Java
// Project: Project 01
// DateDue: 09.14.2016

//package project01;

import java.util.*;

public class Project1 {

    private String Title;
    private ArrayList < Tag > tableContent;
    
    public Project1(){
        
          Title = "IDE and Text Editor";
          tableContent = new ArrayList <Tag> ();
    }
    
    public Project1(String title){
        
          Title = title;
          tableContent = new ArrayList<Tag> ();
    }
        
    public String getTitle(){
        
        return Title;        
    }
    
    public void appendTag(Tag tableTag)
    {
       tableContent.add(tableTag);   
    }
    
    
    @Override
    public String toString(){
        
        StringBuilder htmlstring1 =  new StringBuilder();
        htmlstring1.append("<!DOCTYPE html>");
        htmlstring1.append("<html> \n");
        htmlstring1.append("<head> \n");
        htmlstring1.append("<meta charset = \"Utf = 8\">\n");
        TitleTag tTag =  new TitleTag (getTitle());
        htmlstring1.append(tTag);
        htmlstring1.append("</head>\n");
        htmlstring1.append("<body style =" + "\"background-color:#f9ecec\""+">");
        for(Tag T: tableContent)
             htmlstring1.append(T.toString());
        
        
        htmlstring1.append("<p> </p>");
        htmlstring1.append("</body>");       
        htmlstring1.append("</html>");
        return htmlstring1.toString();
    }
}

