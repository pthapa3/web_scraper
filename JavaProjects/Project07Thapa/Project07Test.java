// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 07
// DateDue: 11.30.2016

//package project07;

import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Application;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;


public class Project07Test  extends Application {
    
    private Connection Conn;
    private ResultSet RS;
    private Statement State;
    private Button DeleteRow; 
    private Label response;
    private Label DeletedCpuLbl;
    private Label ErrorLbl1;
    private ObservableList <CpU> CpuObsList;
    private TableView  <CpU> CpuTable;
    private String CpuToDel;        
    private int Index;
   
    public static void main(String[] args){
       launch(args);
    }
   
   @Override
    public void start(Stage mainStage){
      
     mainStage = new Stage();
     mainStage.setTitle("CPU TableView");
     FlowPane rootNode = new FlowPane(Orientation.VERTICAL, 0, 10);
     rootNode.setAlignment(Pos.CENTER);        
     Scene scene = new Scene(rootNode, 800,600);

     DeleteRow = new Button ("Delete Selected Row");
     response = new Label ();
     DeletedCpuLbl = new Label();
     ErrorLbl1 = new Label();
        

     CpuObsList = FXCollections.observableArrayList(getCpuList());  
     CpuTable = new TableView <>(CpuObsList);
     CpuTable.setEditable(true);
     
     //Setting Table Cell Value for each Column
     TableColumn <CpU,String>  IdCol = new TableColumn <>("ID");
     IdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
     IdCol.setMinWidth(100);
     CpuTable.getColumns().add(IdCol);
     
     TableColumn <CpU,String>  CpuNameCol = new TableColumn <>("CPU Name");
     CpuNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
     CpuNameCol.setMinWidth(400);
     CpuTable.getColumns().add(CpuNameCol);
       
     TableColumn<CpU,String> CpuPerformCol = new TableColumn<>("CPU Performance");
     CpuPerformCol.setCellValueFactory(new PropertyValueFactory<>("performance")); 
     CpuPerformCol.setMinWidth(100);
     CpuTable.getColumns().add(CpuPerformCol);

     TableColumn <CpU,String> CpuPriceCol = new TableColumn<>("CPU Price");
     CpuPriceCol.setCellValueFactory(new PropertyValueFactory<>("price")); 
     CpuPriceCol.setMinWidth(100);
     CpuTable.getColumns().add(CpuPriceCol);
       
     //Selection model for selecting items in the TableView through which
     //we can add listeners to handle events
     TableView.TableViewSelectionModel< CpU > CpuSelectionModel = 
                                                   CpuTable.getSelectionModel();
     CpuSelectionModel.selectedIndexProperty().addListener(
                                                 new ChangeListener< Number >(){
            
              @Override
	      public void changed( ObservableValue< ? extends Number > changed,
                                          Number oldValue, Number newValue){
					
                    CpU tempCpu = CpuObsList.get(newValue.intValue());					                                             
                    CpuToDel = tempCpu.getCpuName();
                    response.setText( "SelectedRow CPU : "+ tempCpu.getCpuName());  
                    Index = CpuSelectionModel.getSelectedIndex();
                                        
              }
    });
                
    // Rendering table cell of each column with cellFactory for enabling 
    // editing features    
    CpuNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
    CpuNameCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent <CpU,String>>(){
                 
        @Override
        public void handle(TableColumn.CellEditEvent<CpU, String> ed){

            DeletedCpuLbl.setText("");
            CpU tempCpu = (CpU) ed.getTableView().getItems()
                           .get(ed.getTablePosition().getRow());
     
            if(ed.getNewValue().isEmpty())
                ErrorLbl1.setText("Error. Please enter data.TextFields"
                                + " cannot be left blank.");
        
            else
	        tempCpu.setCpuName(ed.getNewValue());                                      
				
        }
               
    });  
      
     CpuPerformCol.setCellFactory(TextFieldTableCell.forTableColumn());
     CpuPerformCol.setOnEditCommit(new EventHandler
                                     <TableColumn.CellEditEvent <CpU,String>>(){
                   
        @Override
        public void handle(TableColumn.CellEditEvent<CpU, String> ed){
               
            
            DeletedCpuLbl.setText("");
            CpU tempCpu = (CpU) ed.getTableView().getItems()
                          .get(ed.getTablePosition().getRow());
                                       
            if(ed.getNewValue().isEmpty())
                ErrorLbl1.setText("Error. Please enter data. "
                                    + "TextFields cannot be left blank.");                        
                                    
            else {
                if(!ed.getNewValue().matches("^(?=[0-9])\\d{1,}"))
                    ErrorLbl1.setText("Error. Please enter valid data. "
                                    + "Numbers only. No decimals and commas. ");                                
                                    
                else
		    tempCpu.setCpuPerformance(ed.getNewValue());
            }
                                       
					                       
        }
      });
       
       
     CpuPriceCol.setCellFactory(TextFieldTableCell.forTableColumn());
     CpuPriceCol.setOnEditCommit(new EventHandler
                                     <TableColumn.CellEditEvent <CpU,String>>(){
                                         
        @Override
        public void handle(TableColumn.CellEditEvent<CpU, String> ed){ 
                              
            DeletedCpuLbl.setText("");           
            CpU tempCpu = (CpU) ed.getTableView().getItems()
                                  .get(ed.getTablePosition().getRow());
	             
            if(ed.getNewValue().isEmpty())
                ErrorLbl1.setText("Error. Please enter data. "
                                     + "TextFields cannot be left blank.");

            else if(!ed.getNewValue().matches("^(?=[0-9])\\d{1,}[.?]\\d{2,}"))
                ErrorLbl1.setText("Error. Please enter valid data.Numbers"
                                      + " with at least two decimal places.");
            else
		tempCpu.setCpuPrice(ed.getNewValue());
                       
			
        }
     });     
      
     rootNode.getChildren().addAll(CpuTable, DeleteRow, response,
                                     DeletedCpuLbl, ErrorLbl1);
     DeleteRow.setOnAction(new deleteRecord());
     mainStage.setScene(scene);
     mainStage.show();
       
  }
    
//Method for reading data from database    
public  ArrayList <CpU> getCpuList (){
    
    ArrayList<CpU> ReadCpuList = new ArrayList<>();
    
    //Reading data from Database and adding it to the ArrayList
    try{
        Conn = DriverManager.getConnection("jdbc:mysql://localhost:3306"
                                   + "/project04?useSSL=false", "PrakashSql" ,"mySql2016");
         
        State = Conn.createStatement();
        RS = State.executeQuery("Select * from CpUList");
         
        while(RS.next()){
           CpU tempCpu = new CpU (RS.getInt(1),RS.getString(2), 
                                RS.getInt(3),
                                RS.getDouble(4));
            
           ReadCpuList.add(tempCpu);
           
           //These methods gets the edited value from the table cell
           // and pass it to method updateCpu to update the database
           tempCpu.performanceProperty().addListener(
			            ( O, oldValue, newValue ) -> {
					
			       updateCpu(oldValue, tempCpu.getCpuPerformance());
                                              
                                });
            
           tempCpu.priceProperty().addListener(( O, oldValue, newValue ) -> {
               
		               updateCpu (oldValue, tempCpu.getCpuPrice());
					}
				);
           tempCpu.nameProperty().addListener(( O, oldValue, newValue ) -> {
						
                                updateCpuName (oldValue, tempCpu.getCpuName());
				
                                });
        }
         
    }
    catch(SQLException SE){
     
        SE.printStackTrace();
    }
   
    return ReadCpuList;
    
}

private class deleteRecord implements EventHandler <ActionEvent>{
    
    @Override
    public void handle(ActionEvent Ae){
        
        try{
            Conn = DriverManager.getConnection("jdbc:mysql://localhost:3306"
                    + "/project04?useSSL=false", "PrakashSql", "mySql2016");
            
            State = Conn.createStatement();
            
            State.execute("Delete from cpuList where Cpu_Name = "
                                 + "'"+ CpuToDel + "'");
            
        }
        
        catch(SQLException Se){
        
            Se.printStackTrace();
        }
        
        DeletedCpuLbl.setText("Deleted CPU:" + CpuToDel);
        CpuObsList.remove(Index);     
        
        
    }
    
}

//updates CpuPrice and Performance
public  void updateCpu( String oldValue, String newValue  ){
    
    try{
	Conn = DriverManager.getConnection("jdbc:mysql://localhost:3306"
                                     + "/project04?useSSL=false", "PrakashSql" ,"mySql2016");
	State = Conn.createStatement();
        State.execute( "update CpuList set Performance = '"+ newValue+"'"
                                 +" where Performance ='"+ oldValue+"'" );
        State.execute( "update CpuList set Price ='"+ newValue +"' "
                                 + "where Price =" + oldValue);
                      
    }
    catch( SQLException e ){
        
	e.printStackTrace();
    }
                   
    ErrorLbl1.setText("Database updated.");       

}


// updates Cpu Name
public  void updateCpuName( String oldValue, String newValue  ){

    try{
	Conn = DriverManager.getConnection("jdbc:mysql://localhost:3306"
                                     + "/project04?useSSL=false", "PrakashSql" ,"mySql2016");
	State = Conn.createStatement();
        State.execute( "update CpuList set CPU_Name = '"+ newValue+"'"
                                          +" where CPU_Name ='"+ oldValue+"'" );
                       
    }
    catch( SQLException e ){
	
        e.printStackTrace();
    }                  
                   
    ErrorLbl1.setText("Database updated.");       
	
 } 
}
