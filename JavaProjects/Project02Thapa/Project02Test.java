// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 02
// DateDue: 09.28.2016

//package project02;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;
import java.io.*;
import java.nio.file.Paths;
import java.lang.IllegalStateException;


public class Project02Test {

    private static Scanner input;
    private static Formatter output;
    private static Pattern pattern;
    private static Matcher Match;
    
    
    public static void main(String[] args) {
       
           String ChartobeMatch = " ";                
                 
        //Creating HTML page with Title and Heading
        Project1 Project2Html = new Project1("Phone Numbers");
        Project2Html.appendTag(new HeadingTag("Found Phone Numbers"));
        
        //Create Table
        TableTag  newTable = new TableTag("lightgreen");
        Project2Html.appendTag(newTable);
        //Table Head
        TableRowTag HeadRow = new TableRowTag();
        HeadRow.appendTabRow(new TableHeadItems("Area Code"));
        HeadRow.appendTabRow(new TableHeadItems("Phone Number"));
        Project2Html.appendTag(HeadRow);
        
        
        
        // Open the file       
        try{
              input = new Scanner(Paths.get("Project02Data.txt"));
        }  
        // Error if the file not found
        catch(IOException e ){
                    
                e.printStackTrace();
        }
                  
         //Read the file  
        try{
              input.useDelimiter("\\Z");        
              ChartobeMatch = input.next();
        
        }
        //If the record in the file is not properly formed
        catch( NoSuchElementException Nsee ){
		
              Nsee.printStackTrace();
	}        
        // If the Scanner was closed before the data was input
        catch( IllegalStateException Ise ){
			
              Ise.printStackTrace();
		
        }
        
        //open File to write
        try{
             output = new Formatter("Project02OutputData.html");
             
        }
        // File write permission error
        catch(SecurityException security){
               security.printStackTrace();
            
        }
        catch(FileNotFoundException Fnf){
            
             Fnf.printStackTrace();            
        }
        
        
        //Pattern for extracting valid phone numbers from file
	pattern =  Pattern.compile("([(]\\d{1,3}[)]+\\s+\\d{1,3}+[-]+\\d{4})"
                             + "|(\\d{1,3}[-])+\\d{4}");
        Match =  pattern.matcher(ChartobeMatch);      
       
        ///Extract Phone numbers from the file using pattern
        while (Match.find()){
            
                String PhoneNumber = Match.group();
                 
                System.out.println(PhoneNumber);
                 
                String AreaCodeoutput = "";
                String PhonenumOutput = " ";
                TableDataTag TableData = new TableDataTag();
                
                //Extracting Area code from Found Phone number
                String[] AreaCode=  PhoneNumber.split("-"+"\\d{1,4}|\\s\\d{3}");
                    
                    for(String output : AreaCode){                                   
                         AreaCodeoutput = output;          
                    }
                 TableData.appendTabData(new TableDataItem(AreaCodeoutput));  

                 
                // Extracting Phone number from Found Phone Number 
                String [] Phonenum = PhoneNumber.split("^(\\d{1,3}[-])"
                                                       + "|^[(]\\d{1,3}+[)]");                 
                   
                for(String output : Phonenum){
                          PhonenumOutput = output; 
                   } 
                TableData.appendTabData(new TableDataItem(PhonenumOutput)); 
                
                Project2Html.appendTag(TableData);
                                
        }
         
         //Write to File 
         output.format("%s", Project2Html);
        
         //close Scanner
         input.close();

         //close formatter; Formatter is buffered; if not closed should be flushed. 
         //close() implicitly calls flush.

         output.close();
          
          
    }
}
        
   
    


