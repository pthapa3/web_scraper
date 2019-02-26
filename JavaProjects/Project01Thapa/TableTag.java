// Author: Prakash Thapa
// Class:  Advance Java
// Project: Project 01
// DateDue: 09.14.2016

//package project01;

public class TableTag extends Tag {
    
        private String Color;
        
        // Default Table background color lightgray
        public TableTag(){
           super();
           Color = "lightgray";
        }
       
        // Constructor for creating Table with specific background color 
        public TableTag (String bgColor){
           
           Color = bgColor;
           
        }       
         
        public String getbgColor(){
           
           return Color;          
        }
         
        // Create Table dynamically with Two-dimension Array
        public String CreatenewTable (String [][] RowColData){
            StringBuilder sb = new StringBuilder();
            for(int row = 0; row < RowColData.length ;  row++){
                    sb.append("<tr>");
                for(int col = 0; col < RowColData[row].length; col++){
                    sb.append("<td>");
                    sb.append(RowColData[row][col]);
                    sb.append("</td>\n");
                }                  
            }        
            sb.append("</tr>");
            sb.append ("</table>");
            sb.append("</body>");
            sb.append("</html>");
            return sb.toString();
        }    
            
        @Override // Table with background color and even rows striped.
                  // Highlights table row when hovered 
        public String toString(){
           
           return "<style>" + " table, th, td {border: 1px solid black;" +"\n"
                   + "border-collapse: collapse;} " + "tr:nth-child(even)"
                   + "{background-color: White}"+ "tr:hover {background-color:"
                   + "lightgreen}"+ "</style>"  + "\n"+"<table style= " + "\"" 
                   + "background-color:" + getbgColor()+ "\"" + "\n" + "width= "
                   + "\"auto" + "\""+ " >\n" ;     
        }
       
}
    

