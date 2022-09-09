package gui;

import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

//login form source: https://www.javatpoint.com/login-form-java
public class Authentication extends VBox {

	 //initialize button, panel, label, and text field  
    Button b1;  
    Label userLabel, passLabel;  
    final TextField  textField1;
    final PasswordField textField2;  
    
    private String[] usernames = {"admin"};
    private String[] passwords = {"admin"};
    
    
    private Stage primaryStage;  
    private Scene nextScene;
      
    //calling constructor 
    public Authentication(Stage primaryStage, Scene nextScene)  
    {     
        //set the next page
    	this.primaryStage = primaryStage;
    	this.nextScene = nextScene;
    	
        //create label for username   
        userLabel = new Label();  
        userLabel.setText("Username");      //set label value for textField1
          
        //create text field to get username from the user  
        textField1 = new TextField();
  
        //create label for password  
        passLabel = new Label();  
        passLabel.setText("Password");      //set label value for textField2  
          
        //create text field to get password from the user  
        textField2 = new PasswordField();
          
        //create submit button  
        b1 = new Button("SUBMIT"); //set label to button  
          
        //create panel to put form elements  
        this.getChildren().addAll(userLabel, textField1, passLabel, textField2, b1);
          
        //perform action on button click   
        b1.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent arg0) {
        		verifyCredentials(textField1.getText(), textField2.getText());
        	}
        });
    }
	 
    
    //verify the credentials entered by the user
    public void verifyCredentials(String inputUsername, String inputPassword) {
    	for (int i = 0; i < this.usernames.length; i++) {
    		if (this.usernames[i].equals(inputUsername) && this.passwords[i].equals(inputPassword)) {
    			System.out.println("Welcome, user");
    			primaryStage.setScene(nextScene);
    			return;
    		}    	
    	}
    	System.out.println("Please enter valid username and password");  
    }
		 

}
