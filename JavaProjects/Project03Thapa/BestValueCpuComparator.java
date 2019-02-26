// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 03
// DateDue: 10.12.2016


//package project03;

import java.util.*;

public class BestValueCpuComparator implements Comparator <CpU>{
    
    
         @Override 
         public int compare(CpU First, CpU Second){
             double FirstCpu = (double)(First.getIntPerformance());
             double result1  = FirstCpu/First.getdoubleCpuPrice();
             
             double SecondCpu = (double)(Second.getIntPerformance());
             double result2 = SecondCpu/Second.getdoubleCpuPrice();
             
            return (int)(result1-result2);
             
         }
    
}
