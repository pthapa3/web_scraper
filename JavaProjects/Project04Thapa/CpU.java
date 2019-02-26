// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 04
// DateDue: 10.26.2016

//package project04test;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;




public class CpU {
          
    private String CpuName;
    private String Performance;
    private String price ;
   
      
   
    public CpU (String Cpu, String CpuPerformance, String cpuPrice){
    
        CpuName = Cpu;
        Performance = CpuPerformance;
        setcpuPrice(cpuPrice);
    }
    
    
      
    public void setcpuPrice(String cpuPrice){
          
        if (cpuPrice.equals("\r")){
            price = "0.00";
        }       
        
        else{
            String cPrice = cpuPrice.replaceAll("\\s[,]", "");
            price = cPrice;
        }
    }
   
    
   
      
    public String getCpuName(){
            return CpuName;
    }
    
    public String getCpuPerformance(){
            return Performance;
    }
    
    public String getCpuPrice(){
            return price;
    }
    
    public double getdoubleCpuPrice(){
                  
            double cpuPrice = Double.parseDouble(getCpuPrice());
          
            return cpuPrice;
    }


    public int getIntPerformance(){
            NumberFormat I = NumberFormat.getNumberInstance(Locale.US); 
           
            int IntPerformance;
            Number n = 0;
            try{
                n = I.parse(Performance);
            }
            catch(ParseException Pe){
                Pe.printStackTrace();
            }
            IntPerformance = n.intValue();
            return IntPerformance;
    }
  
   
    
    @Override      
    public String toString(){
        
        return String.format("%s%,.2f",CpuName + ": " + "$", getdoubleCpuPrice())  ;
    }
    
}   
            
            
        
      

     
          
          
      
            
    
