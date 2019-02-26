// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 07
// DateDue: 11.30.2016


//package project07;



import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class CpU {
          
    private StringProperty CpuName;
    private StringProperty Performance;
    private StringProperty price ;  
    private IntegerProperty Id;
   
   
    public CpU (String Cpu, String CpuPerformance, String cpuPrice){
    
        CpuName = new SimpleStringProperty(Cpu);
        Performance = new SimpleStringProperty();
        price = new SimpleStringProperty ();       
    }
    
    public CpU (int id, String Cpu, int CpuPerformance, double cpuPrice){
    
        CpuName = new SimpleStringProperty(Cpu);
        Performance = new SimpleStringProperty(Integer.toString(CpuPerformance));
        price = new SimpleStringProperty (Double.toString(cpuPrice));  
        Id = new SimpleIntegerProperty(id);
    }
   
    
    public void setCpuPerformance (String CpuPerformance){   
        
        Performance.set(CpuPerformance);
        
    }
    
    public void setCpuPrice (String CpuPrice){   
        
        price.set(CpuPrice);
        
    }
    
    public void setCpuName (String Name){   
        
        CpuName.set(Name);
        
    }
    
    public IntegerProperty idProperty(){
            return Id;
    }
       
    public StringProperty nameProperty(){
            return CpuName;
    }
    
    public  StringProperty performanceProperty(){
            return Performance;
    }
    
    public StringProperty priceProperty(){
            return price;
    }
    
    public String getCpuName(){
            return CpuName.get();
    }
    
   public String getCpuPerformance(){
            return Performance.get();
    }
    
    public String getCpuPrice(){
            return price.get();
    }
    
    
     
    @Override      
    public String toString(){
      
        return String.format("%s%.2f", getCpuName() + ":  " 
                + getCpuPerformance() +" $", getCpuPrice())  ;
    }
    
}   
            
    