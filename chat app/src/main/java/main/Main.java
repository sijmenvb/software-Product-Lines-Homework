package main;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
	
	private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0 ;
    private boolean arrayFilled = false ;
	
    public static void main(String[] args) {
        //launch(args);
        AuthenticationScreen();
    }
    
    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        Pane root = new Pane();
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
        
        //frame rate source: https://stackoverflow.com/questions/28287398/what-is-the-preferred-way-of-getting-the-frame-rate-of-a-javafx-application
        AnimationTimer frameRateMeter = new AnimationTimer() {

            @Override
            public void handle(long now) {
                long oldFrameTime = frameTimes[frameTimeIndex] ;
                frameTimes[frameTimeIndex] = now ;
                frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length ;
                if (frameTimeIndex == 0) {
                    arrayFilled = true ;
                }
                if (arrayFilled) {
                    long elapsedNanos = now - oldFrameTime ;
                    long elapsedNanosPerFrame = elapsedNanos / frameTimes.length ;
                    double frameRate = 1000000000.0 / elapsedNanosPerFrame ;
                    primaryStage.setTitle(String.format("Current frame rate: %.3f", frameRate));
                }
            }
        };

        frameRateMeter.start();
    }
    
    //login form source: https://www.javatpoint.com/login-form-java
    public static void AuthenticationScreen() {
    	try  
        {  
    		//create JPanel where the user should go after authentication
    		JPanel nextPage = new JPanel();
    		nextPage.setSize(1080,720);                         
    		
            //create instance of the Authentication      		
    		
            Authentication form = new Authentication(nextPage);  
            form.setSize(1080,720);  //set size of the frame  
            form.setVisible(true);  //make form visible to the user  
        }  
        catch(Exception e)  
        {     
            //handle exception   
            JOptionPane.showMessageDialog(null, e.getMessage());  
        }  
    }
}