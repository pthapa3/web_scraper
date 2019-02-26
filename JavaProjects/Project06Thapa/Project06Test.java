// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 06
// DateDue: 11.16.2016

//package project06;

import java.sql.*;
import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.geometry.*;
import javafx.event.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import java. util.*;



public class Project06Test extends Application { 
    
    private Button upload;      
    private static Connection Connect;
    private static Statement Statem;
    private static ResultSet RS;
    private TextField AddCputxt;
    private TextField AddCpuPerformtxt;
    private TextField AddCpuPricetxt;
    private ObservableList <CpU> CpuObsList;
    private Label ErrorLbl1;
    private Label ErrorLbl2;
    private Label ErrorLbl3;

    

    @Override
    public void init(){
        System.out.println("System initialized...");
    }
    
   
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override 
    public void start(Stage mainStage){
        mainStage = new Stage ();
        mainStage.setTitle("CPU List");
        FlowPane root = new FlowPane(Orientation.VERTICAL, 0, 10);
        root.setAlignment(Pos.CENTER);       
        Scene scene = new Scene(root, 800,600);
        
        ErrorLbl1 = new Label("");
        ErrorLbl2 = new Label("");
        ErrorLbl3 = new Label("");
       
        Label ListHeadLbl = new Label ("CPU_Name, Cpu_Performance, Cpu_Price");       
         
        Label updateCpu =  new Label(" For adding new Cpu in the Database List"
                                    + "\n Please enter the Cpu name:");
               
        Label CpuPerformLbl =  new Label ("Please enter Cpu performance level"
                                          + " number:");
        Label CpuPrice = new Label ("Please enter Cpu Price:");
        
        AddCputxt = new TextField ();
        AddCpuPerformtxt =  new TextField();
        AddCpuPricetxt = new TextField();        
       
        upload = new Button("uploadData");   
        
       
        CpuObsList = FXCollections.observableArrayList(getCpuList());
        ListView <CpU> CpuListview  = new ListView <> ();
        CpuListview.setItems(CpuObsList);
        CpuListview.setPrefSize(600, 200);
        
        root.getChildren().addAll(ListHeadLbl,CpuListview, updateCpu, AddCputxt,
                            CpuPerformLbl, AddCpuPerformtxt,ErrorLbl1, CpuPrice,
                            AddCpuPricetxt,ErrorLbl2, upload, ErrorLbl3);
        
        
        mainStage.setScene(scene);
        upload.setOnAction(new InsertData());
        mainStage.show();
    }
    
    
    public static ArrayList <CpU> getCpuList (){ 
        
      ArrayList <CpU> ReadcpuList = new ArrayList<>();

      try{		
	   // Connecting to Database
           // Database, username, password
	  Connect = DriverManager.getConnection( "jdbc:mysql://localhost:3306/"
                                     + "project04?useSSL=false", "PrakashSql" ,"mySql2016");
	   
          Statem = Connect.createStatement();                     
           
          //Reading data from database
          RS = Statem.executeQuery( "Select * from CpUList" );
          
	  while(RS.next()){
                ReadcpuList.add(new CpU(RS.getString(2), 
                                RS.getInt(3),
                                RS.getDouble(4)));              
	  }			
       } 
     
       catch( SQLException e ){
	    e.printStackTrace();
       }     
     
      return ReadcpuList;
    }


    public boolean  DataValidation(){
    
        boolean result = true;
    
        if((AddCputxt.getText().isEmpty())
             |(AddCpuPricetxt.getText().isEmpty())
             |(AddCpuPerformtxt.getText().isEmpty())){
         
             ErrorLbl3.setText("Error. TextFields cannot be left blank.");        
             ErrorLbl1.setText("");
             ErrorLbl2.setText(""); 
         
             result = false;
        }
        
        else{
             ErrorLbl3.setText("");
         
             if(!AddCpuPerformtxt.getText().matches("^(?=[0-9])\\d{1,}")){
         
                  ErrorLbl1.setText("Error. Please enter valid data. "
                          + "Numbers only. No decimals and commas. ");
                  result = false;
             }
             else
                  ErrorLbl1.setText("");
        
             if(!AddCpuPricetxt.getText().matches("^(?=[0-9])\\d{1,}[.?]"
                                                  + "\\d{2,}")){
             
                  ErrorLbl2.setText("Error. Please enter valid data."
                                + " Numbers with at least two decimal digits.");
                  result = false;
             }
             else 
                  ErrorLbl2.setText("");
        
        }
        
      return result;
    }


    private class InsertData implements EventHandler <ActionEvent>{
   
   
        @Override
        public void handle(ActionEvent AE){   
        
            if(DataValidation()){
                CpU newCpu = new CpU(AddCputxt.getText(), 
                       Integer.parseInt(AddCpuPerformtxt.getText()), 
                       Double.parseDouble(AddCpuPricetxt.getText()));               
         
                try{    
                   
                 Connect = DriverManager.getConnection("jdbc:mysql://localhost:"
                                 + "3306/project04?useSSL=false", "PrakashSql", "mySql2016");
             
                 Statem = Connect.createStatement();
             
                 Statem.execute("insert into CpuList(CPU_Name, Performance,"
                                 + " Price)" + "values('" + newCpu.getCpuName()
                                 + "', " + newCpu.getIntPerformance() + ","
                                 + newCpu.getdoubleCpuPrice() +")" );
             
                }
              catch(SQLException E){
                  E.printStackTrace();
              }
                
                //Updating ObservableList and displaying in
                //the Listview
                CpuObsList.add(newCpu);
                ErrorLbl3.setText("Success. Data sent to Database");
                System.out.println("Valid Data. Data inserted into Database.");
            }                 
            else  
                System.out.println("Error. Invalid Data..");
        
              
         AddCputxt.clear();
         AddCpuPerformtxt.clear();
         AddCpuPricetxt.clear();
      }
    
  }
}
