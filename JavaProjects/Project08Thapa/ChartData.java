// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 08
// Due Date: 12.13.2016

//package project08;



public class ChartData {
    
    private String Semester;
    private String course;
    private int points;
    
    public ChartData(String Semester, String course, int points){
        
        this.Semester = Semester;
        this.course = course;
        this.points = points;
        
    }
    
    public String getSemester (){
        return Semester;
    }
    public String getCourse (){
        return course;
    }
    public int getNumber (){
        return points;
    }
    
 
    @Override
    public String toString(){
        return Semester + " " + course + " " + points;
     }
    
    
}
