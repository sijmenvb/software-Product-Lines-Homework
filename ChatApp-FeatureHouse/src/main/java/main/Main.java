package main; 

import java.io.File; 
import java.util.LinkedList; 

import backend.ServerConnection; 
import main.AuthenticationInterface; 
import javafx.animation.AnimationTimer; 
import javafx.application.Application; 
import gui.GUI; 
import javafx.scene.Scene; 
import javafx.stage.Stage; 
import logging.NullLogging; 

import authentication.NoUserAuthentication; 

import main.LoggingInterface; 

public   class  Main  extends Application {
	
	public static void main(String[] args) {	
			launch(args);
	}

	

	@Override
	public void start(final Stage primaryStage) {
		// unfortunately we have to load the ui again since we can not pass it in a
		// static context.
		
		
		UIInterface ui = new GUI();
		AuthenticationInterface auth = getAuthenticator();
		LoggingInterface logger = getlogger();
		
		ui.javaFXStart(primaryStage, auth, logger);
	}

	
	private static AuthenticationInterface getAuthenticator() {
		return new NoUserAuthentication();
	}

	
	private static LoggingInterface getlogger() {
		return new NullLogging();
	}


}
