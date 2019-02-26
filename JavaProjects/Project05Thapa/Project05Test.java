// Author: Prakash Thapa
// Class:  Advanced Java
// Project: Project 05
// DateDue: 11.02.2016

//package project05;

import javafx.application.*;
import javafx.event.*;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class Project05Test extends Application {
    
    private Button B1;
    private Label pwdChkLbl; 
    private Label pwdChkLbl1;
    private Label pwdChkLbl2 ;
    private Label pwdChkLbl3 ;
    private Label pwdChkLbl4 ;
    private TextField userPwdTxt;    
    private TextField userNameTxt;
    
    @Override
    public void init(){
            System.out.println("Program Starting....");
    }        
    
    public static void main(String[] args){        
        System.out.println("System Initializing.....");
        launch(args);           
    }
    
    @Override
    public void start( Stage PrimaryStage){
            
        PrimaryStage.setTitle("Check my Password");
        FlowPane root = new FlowPane(Orientation.VERTICAL, 20, 10);
        root.setAlignment(Pos.CENTER);

        Label UserNameLbl = new Label("Enter your Username:");        
        Label PasswordLbl = new Label ("Enter your Password:");    
        
        B1 = new Button();
        B1.setText("Login");  
  
        pwdChkLbl = new Label(); 
        pwdChkLbl1 = new Label();
        pwdChkLbl2 = new Label();
        pwdChkLbl3 = new Label();
        pwdChkLbl4 = new Label();
        
        userNameTxt = new TextField();
        userPwdTxt = new TextField();      
        
           
        
        root.getChildren().addAll(UserNameLbl, userNameTxt, PasswordLbl, 
                userPwdTxt, B1, pwdChkLbl, pwdChkLbl1, pwdChkLbl2, pwdChkLbl3, pwdChkLbl4);
        PrimaryStage.setScene(new Scene (root, 400, 400));
        PrimaryStage.show();
                
      B1.setOnAction(new passWordChkHandler());     
    }


 private class passWordChkHandler implements EventHandler <ActionEvent>{
        
       @Override 
       public void handle(ActionEvent Ae){                        
        
        //Password Validity: Checking for atleast 1 lowercase
        // 1 uppercase, 1 special character and 1 digit [0-9]

        if (userPwdTxt.getText().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])"
                                      + "(?=.*[!@#$%&?*]).*")){
             pwdChkLbl.setText("Password Correct. Login Successful");
             B1.setText("Login");
             pwdChkLbl1.setText(""); 
             pwdChkLbl2.setText("");
             pwdChkLbl3.setText("");
             pwdChkLbl4.setText("");
        }    


        else{
             pwdChkLbl.setText("Invalid Password. ");
             B1.setText("Retry password");
             if (!userPwdTxt.getText().matches("^(?=.*[a-z]).*")){
                  pwdChkLbl1.setText(" Your password needs at least one "
                                       + "lowercase character. ");                  
             }
             else
                  pwdChkLbl1.setText("");

            if (!userPwdTxt.getText().matches("^(?=.*[A-Z]).*")){
                   pwdChkLbl2.setText("Your password needs at least "
                           + "one Uppercase character. ");
                  
            }

            else  pwdChkLbl2.setText("");

            if (!userPwdTxt.getText().matches("^(?=.*[!@#$%^&*]).*")){               
                   pwdChkLbl3.setText("Your password needs at least one"
                          + " special character. ");
                  
            }

            else pwdChkLbl3.setText("");

            if (!userPwdTxt.getText().matches("^(?=.*[0-9]).*")) {              
                  pwdChkLbl4.setText("Your password needs at least one"
                                     + " digit character. ");                  
            }
            else
                  pwdChkLbl4.setText("");               

        }// End else          
             
        Ae.consume();
        
    } // End  handle

  } // End passWordChkHandler  
 
} //End Project05
           
    
