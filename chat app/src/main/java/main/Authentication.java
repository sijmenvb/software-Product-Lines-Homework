package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Authentication extends JFrame implements ActionListener {

	 //initialize button, panel, label, and text field  
    JButton b1;  
    JPanel newPanel;  
    JLabel userLabel, passLabel;  
    final JTextField  textField1, textField2;  
    
    private String[] usernames = {"admin"};
    private String[] passwords = {"admin"};
    
    
    private JPanel nextPage;
      
    //calling constructor  
    Authentication(JPanel nextPage)  
    {     
        //set the next page
    	this.nextPage = nextPage;
    	
        //create label for username   
        userLabel = new JLabel();  
        userLabel.setText("Username");      //set label value for textField1
        userLabel.setPreferredSize(new Dimension(100,20));
        userLabel.setSize(100, 20);
          
        //create text field to get username from the user  
        textField1 = new JTextField(15);    //set length of the text  
        textField1.setPreferredSize(new Dimension(10,2));
        textField1.setSize(100, 20);
  
        //create label for password  
        passLabel = new JLabel();  
        passLabel.setText("Password");      //set label value for textField2  
          
        //create text field to get password from the user  
        textField2 = new JPasswordField(15);    //set length for the password  
          
        //create submit button  
        b1 = new JButton("SUBMIT"); //set label to button  
          
        //create panel to put form elements  
        newPanel = new JPanel(new GridLayout(3, 1));  
        newPanel.add(userLabel);    //set username label to panel  
        newPanel.add(textField1);   //set text field to panel  
        newPanel.add(passLabel);    //set password label to panel  
        newPanel.add(textField2);   //set text field to panel  
        newPanel.add(b1);           //set button to panel  
          
        //set border to panel   
        add(newPanel, BorderLayout.CENTER);  
          
        //perform action on button click   
        b1.addActionListener(this);     //add action listener to button  
        setTitle("LOGIN FORM");         //set title to the login form 
    }
	 
  //define abstract method actionPerformed() which will be called on button click   
    public void actionPerformed(ActionEvent ae)     //pass action listener as a parameter  
    {  
        String userValue = textField1.getText();        //get user entered username from the textField1  
        String passValue = textField2.getText();        //get user entered pasword from the textField2  
          
        //check whether the credentials are authentic or not  
        if (verifyCredentials(userValue, passValue)) {  //if authentic, navigate user to a new page  
                        
            //switch to next page
        	this.getContentPane().removeAll();
        	this.getContentPane().add(this.nextPage);
        	this.getContentPane().revalidate(); 

        }  
        else{  
            //show error message  
            System.out.println("Please enter valid username and password");  
        }  
    } 
    
    //verify the credentials from the user
    public boolean verifyCredentials(String inputUsername, String inputPassword) {
    	for (int i = 0; i < this.usernames.length; i++) {
    		if (this.usernames[i].equals(inputUsername) && this.passwords[i].equals(inputPassword)) {
    			return true;
    		}
    	}
    	return false;
    }
		 

}
