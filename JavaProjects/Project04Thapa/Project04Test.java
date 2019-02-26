// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 04
// DateDue: 10.26.2016

//package project04test;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class Project04Test {

    private static Scanner input;
        
    public static void main(String[] args) {           
	
	Connection con = null;
	Statement st = null ;
	ResultSet res = null;          
             
        List <CpU> CpuList = new ArrayList<>();
        List <CpU> ReadcpuList = new ArrayList<>();

        // Opening CSV file to read 
        try{
          input = new Scanner(Paths.get("Project04Data.csv"));
        }

        catch(IOException e){
            e.printStackTrace();
        }
                        
        input.nextLine();// Skips the first line, i.e. Heading of the file
        input.useDelimiter(Pattern.compile("[,][\"]|[\"]+[,][$]|[\"]"
                           + "[,]\\w.[,]|\n"));    
        
        //Adding csv data in the List
        while(input.hasNext()){
            
            CpuList.add(new CpU(input.next(), input.next(), input.next()));
            
        
        }
         
        //Close the Scanner     
        input.close();
        
        //Connecting to database
        try{		
	    // Database, username, password
	    con = DriverManager.getConnection( "jdbc:mysql://localhost:3306/"
                                           + "project04?autoReconnect=true&useSSL=false", "PrakashSql" ,"mySql2016");
	    st = con.createStatement(); 
             
            //Storing data in the database from CpuList                 
                
            for (int index =0;index<CpuList.size();index++){                
                st.execute("insert into CpUList (CPU_Name, Performance, Price)"
                        + "values('" + CpuList.get(index).getCpuName() + "'," 
                        + CpuList.get(index).getIntPerformance() + ","
                        + CpuList.get(index).getdoubleCpuPrice()+")" );
            } 
              
           
            //Reading data from database
            res = st.executeQuery( "SELECT * from CpUList" );
	    while(res.next()){
                ReadcpuList.add(new CpU(res.getString(2), 
                                Integer.toString(res.getInt(3)),
                                Double.toString(res.getDouble(4))));              
	    }
			
        }               
                
        catch( SQLException e ){
	    e.printStackTrace();
        }
                
        //Displaying data retrieved from database      
        ReadcpuList.stream().filter(p->p.getdoubleCpuPrice()>0.00)
                                   .sorted(new CpuPriceComparator())                                   
                                   .forEach(t-> System.out.println(t));
    }  
    
}



                  
        