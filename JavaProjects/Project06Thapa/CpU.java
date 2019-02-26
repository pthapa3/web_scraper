// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 06
// DateDue: 11.16.2016


//package project06;


public class CpU {
          
    private String CpuName;
    private String Performance;
    private String price ;  
    private int IntCpuPerformance;
    private double doubleValueCpuPrice; 
         
   
    public CpU (String Cpu, String CpuPerformance, String cpuPrice){
    
        CpuName = Cpu;
        Performance = CpuPerformance;
        price = cpuPrice;       
    }
    
    public CpU (String Cpu, int CpuPerformance, double cpuPrice){
    
        CpuName = Cpu;
        IntCpuPerformance = CpuPerformance;
        doubleValueCpuPrice = cpuPrice;
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
                  
            return doubleValueCpuPrice;
    }
    public int getIntPerformance(){
           
            return IntCpuPerformance;
    }
     
    @Override      
    public String toString(){
      
        return String.format("%s%.2f", getCpuName() + ":  " 
                + getIntPerformance() +" $", getdoubleCpuPrice())  ;
    }
    
}   
            
    