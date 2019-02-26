// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 03
// DateDue: 10.12.2016


//package project03;

import java.util.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.regex.Pattern;



public class Project03Test {         
    
        private static Scanner input;
        
    public static void main(String[] args) {                     

        List <CpU> CpuList = new ArrayList<>();

        // Opening CSV file to read 
        try{
         input = new Scanner(Paths.get("Project03Data.csv"));
        }

        catch(IOException e){
            e.printStackTrace();
        }
                        
        input.nextLine(); //Skips the first Line of text
        input.useDelimiter(Pattern.compile("[,][\"]|[\"]+[,][$]|[\"]"
                           + "[,]\\w.[,]|\n"));
            
        //Reading CSV file and adding to CpuList
        while(input.hasNext()){ 

         CpuList.add(new CpU(input.next(), input.next(), input.next()));

        }
           
        System.out.printf("%n"); 
        System.out.println("CPU Compare Program: ");         

        //Average price of Cpu in the CpuList
        double Average = CpuList.stream().mapToDouble(CpU::getdoubleCpuPrice)
                          .average().getAsDouble();

        System.out.printf("%s%.2f%n", "Average Price: $", Average );          

        //Lowest price of Cpu in the CpuList
        double Min = CpuList.stream().filter(d-> d.getdoubleCpuPrice()>0.00)
               .mapToDouble(CpU::getdoubleCpuPrice).min().getAsDouble();         

        System.out.printf("%s%.2f%n", "Lowest Price: $", Min );

          //Highest price of Cpu in the CpuList
        double Max = CpuList.stream().mapToDouble(CpU::getdoubleCpuPrice)
                       .max().getAsDouble();

        System.out.printf("%s%.2f%n", "Highest Price: $", Max );
        System.out.println(" ");


        System.out.println("Best Value CPU in the List: ");
        System.out.println(CpuList.stream()
                           .filter(d-> d.getdoubleCpuPrice()>0.00)
                           .max(new BestValueCpuComparator()).get());

        //Scanner Closed
        input.close();
          
    }
}
