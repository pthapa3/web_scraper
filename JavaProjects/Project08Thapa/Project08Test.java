// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 08
// Due Date: 12.13.2016


//package project08;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import java.util.Scanner;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;


public class Project08Test extends Application {

    
    private Desktop myDesktop;
    private Scanner readFile;    
    private String selFile;
    private List <ChartData> DataList;
    private File selectedFile;
    private Label fileLbl;
   
    
    
    public static void main(String[] args) {
       launch(args);
       
    }
    
    
    @Override
    public void start(Stage mainStage){
       
        mainStage.setTitle("ChartView");
        BorderPane rootNode = new BorderPane();       
        Scene scene = new Scene(rootNode,600,600);
        fileLbl = new Label ("");
        
        MenuBar mBar = new MenuBar();        
        
        Menu mFile =  new Menu ("File");               
        MenuItem mItemOpenFile = new MenuItem("Open new file"); 
        MenuItem saveFile = new MenuItem("Save Current Chart");
        MenuItem selectFileforChart = new MenuItem("Select File for Chartview");
        MenuItem exit = new MenuItem("Exit");          
        
        mFile.getItems().addAll(mItemOpenFile,selectFileforChart,
                                 saveFile, exit); 
       
        Menu ViewChart = new Menu("ViewChart");
        MenuItem bChart = new MenuItem("Bar Chart");
        MenuItem areaChart = new MenuItem("Area Chart");
        MenuItem myLineChart = new MenuItem("Line Chart");
        MenuItem StackedAreaChart = new MenuItem("StackedArea Chart");         
        ViewChart.getItems().addAll(bChart,areaChart, myLineChart, 
                                    StackedAreaChart);
               
        mBar.getMenus().addAll(mFile,ViewChart);
        
        //Open File
        mItemOpenFile.setOnAction((Ae)-> {
                FileChooser Fc = new FileChooser();
                Fc.setTitle("Open File");
                Fc.getExtensionFilters().addAll(
                new ExtensionFilter("Text Files", "*.txt"),
                new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                new ExtensionFilter("All Files", "*.*"));
                File openFile = Fc.showOpenDialog(mainStage);
                if(openFile !=null){
                     DisplayFile(openFile);
                }            
            });    
                        
        
        //Save current chart as png file
        saveFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent Ae) {
                FileChooser Fc = new FileChooser();
                Fc.setTitle("Save Image");
                Fc.getExtensionFilters().addAll(                     
                        new ExtensionFilter("Image Files", "*.png"));
                
                if(selectedFile==null){
                     Alert alert = new Alert( AlertType.INFORMATION );
	             alert.setTitle( "Error" );
	             alert.setHeaderText( "Please Select the file"
                     + " in the File Menu for ChartView. And Click on the ViewChart option\n"
                     + " to get the Chart before attempting to SAVE current Chart." );
	             alert.show();	
              
                }
                
                else{
                    File sFile = Fc.showSaveDialog(mainStage);
                
                    if(sFile !=null){                     
                          
                          WritableImage snapShot = scene.snapshot(null);
                       try{
                        
                          ImageIO.write(SwingFXUtils.fromFXImage(snapShot, null),
                                                                 "png", sFile);
                        }
                        catch (IOException e) {
                        throw new RuntimeException(e);
                        }
                    }       
                }
            }
        });
            
             
        
        // Selecting File for ChartView        
        selectFileforChart.setOnAction(Ae->{
            
               FileChooser fileChoose = new FileChooser();
               fileChoose.setTitle("Select File for creating Chart");
               selectedFile = fileChoose.showOpenDialog(mainStage);
               if(selectedFile !=null){
                  selFile = selectedFile.toString(); 
                  fileLbl.setText("Your Selected File for ChartView is " +
                                                        selectedFile.getName());
                  ReadFile(selFile);
               }           
        });             
      
        
        //Setting up x-Axis and y-Axis for Chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Course");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Score");          
                      
        
        //Bar Chart
        bChart.setOnAction(ae->{             
               BarChart <String,Number> myChart = new BarChart<>(xAxis,yAxis);
               myChart.setTitle("Bar Chart"); 
               
               if(createChart(myChart)){ 
                      rootNode.setCenter(myChart);
               }
        });
        
        
        areaChart.setOnAction(ae->{              
               AreaChart <String,Number> myChart = new AreaChart<>(xAxis,yAxis);
               myChart.setTitle("Area Chart"); 
               
               if(createChart(myChart)){ 
                      rootNode.setCenter(myChart);
               }    
            
        });
       
        
        myLineChart.setOnAction(ae->{
            
               LineChart <String,Number> myChart = new LineChart<>(xAxis,yAxis);
               myChart.setTitle("Line Chart");
               
               if(createChart(myChart)){ 
                      rootNode.setCenter(myChart);
                }
            
        });
       
        StackedAreaChart.setOnAction(ae->{ 
             
               StackedAreaChart <String,Number> myChart = 
                                            new StackedAreaChart<>(xAxis,yAxis);
               myChart.setTitle("StackedArea Chart");
                
               if(createChart(myChart)){ 
                      rootNode.setCenter(myChart);
                }
        });
        
        //Close application
        exit.setOnAction((ae)->Platform.exit());    
        
        rootNode.setTop(mBar);        
        rootNode.setBottom(fileLbl);
        mainStage.setScene(scene);
        mainStage.show();         
        
}
    
    
    public boolean createChart (XYChart <String, Number> myChart){
        
        boolean result;

        if(selectedFile==null){
              Alert alert = new Alert( AlertType.INFORMATION );
	      alert.setTitle( "Error" );
	      alert.setHeaderText( "Please Select the file"
                      + " in the File Menu before selecting ChartView" );
	      alert.show();	
              result =false;
        }
        
        //Create Series
        else{           
                
              XYChart.Series <String,Number> Winter = new XYChart.Series<>();
              XYChart.Series <String,Number> Spring = new XYChart.Series<>();
              XYChart.Series <String,Number> Summer = new XYChart.Series<>(); 
              XYChart.Series <String,Number> Fall =   new XYChart.Series<>();
        
              Winter.setName("Winter");
              Spring.setName("Spring");
              Summer.setName("Summer");
              Fall.setName("Fall");
        
              myChart.getData().add(Winter);
              myChart.getData().add(Spring);
              myChart.getData().add(Summer);
              myChart.getData().add(Fall);

              // Extracting data from the DataList for creating Chart
              for(ChartData Data : DataList){
                  
                   if (Data.getSemester().equals("Winter")){
                       Winter.getData().add(new XYChart.Data<>(Data.getCourse(),
                                                             Data.getNumber())); 
                   }    
                       
                   else if (Data.getSemester().equals("Spring"))
                       Spring.getData().add(new XYChart.Data<>(Data.getCourse(),
                                                             Data.getNumber()));
                   else if (Data.getSemester().equals("Summer"))
                       Summer.getData().add(new XYChart.Data<>(Data.getCourse(),
                                                             Data.getNumber()));
                   else 
                       Fall.getData().add(new XYChart.Data<>(Data.getCourse(),
                                                             Data.getNumber()));     
              }
              result = true;           
        }
        
        myChart.setAnimated(true);

        return result; 
    }
             
    
    // Opens Selected File
    public void DisplayFile(File openFile){
        
        myDesktop = Desktop.getDesktop();
           
           try{
                myDesktop.open(openFile);
           }
           catch(IOException IO){
            IO.printStackTrace();
           }      
    }

        
    public void ReadFile(String file){    
        
        DataList = new ArrayList<>();
         
        //Open selected file to read
        try{
            
            readFile = new Scanner(Paths.get(file));                   
        }
        catch(IOException Ie){
            Ie.printStackTrace();
        }
          
        //Read selected file's contents and add to an ArrayList
        try{
            readFile.useDelimiter(",|\r\n");  
             
            while(readFile.hasNext()){                     
                   
                    DataList.add(new ChartData(readFile.next(),readFile.next(),
                                                           readFile.nextInt()));
            }
        
        }
        catch(NoSuchElementException Nse){
            Nse.printStackTrace();
        }  
   }

}// End Project08

