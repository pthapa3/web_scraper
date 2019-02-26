// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 04
// DateDue: 10.26.2016

//package project04test;

import java.util.Comparator;


public class CpuPriceComparator implements Comparator <CpU>{
    
    
        @Override 
        public int compare(CpU First, CpU Second){           
             
            return (int)(Second.getdoubleCpuPrice()-First.getdoubleCpuPrice());
             
        }
    
}