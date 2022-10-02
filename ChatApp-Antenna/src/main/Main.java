package main;

import backend.ServerConnection;
import gui.Authentication;
import gui.ChatWindow;
import gui.GUI;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application
{
	public static void main(String[] args) {
		
		UIInterface ui = new GUI();
		
		if (ui.usesJavafx()) {
			launch(args);
		}else {
			ServerConnection serverConnection = new ServerConnection(ui);
		}
	}

	@Override
	public void start(final Stage primaryStage) {
		UIInterface ui = new GUI();
		ui.javaFXStart(primaryStage);
	}
}
