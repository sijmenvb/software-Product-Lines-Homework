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

public class Main extends Application {
	public static void main(String[] args) {	
			launch(args);
	}

	@Override
	public void start(final Stage primaryStage) {
		// unfortunately we have to load the ui again since we can not pass it in a
		// static context.
		
		UIInterface ui = new GUI();
		LoggingInterface logger = getlogger();
		AuthenticationInterface auth = getAuthenticator();
		
		ui.javaFXStart(primaryStage, auth, logger);
	}
}
