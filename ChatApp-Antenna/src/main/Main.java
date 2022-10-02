package main;

import java.io.File;
import java.util.LinkedList;

import backend.ServerConnection;
import cli.CLI;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String[] args) {
		/*
		File pluginFolder = new File("Plugins");
		pluginFolder.mkdir();

		LinkedList<UIInterface> uiList = PluginLoader.loadClasses(pluginFolder, UIInterface.class);
		if (uiList.isEmpty()) {
			System.err.println("APPLICATION REQUIRES UI PLUGIN!");
			return;
		}

		UIInterface ui = uiList.getFirst();
		/*/
		UIInterface ui = new CLI();

		//*/
		if (ui.usesJavafx()) {
			launch(args);
		} else {
			ui.start();
		}
	}

	@Override
	public void start(final Stage primaryStage) {
		//*
		File pluginFolder = new File("Plugins");
		pluginFolder.mkdir();

		LinkedList<UIInterface> uiList = PluginLoader.loadClasses(pluginFolder, UIInterface.class);
		if (uiList.isEmpty()) {
			System.err.println("APPLICATION REQUIRES UI PLUGIN!");
			return;
		}

		UIInterface ui = uiList.getFirst();
		/*/
		UIInterface ui = new GUI();

		// */
		ui.javaFXStart(primaryStage);
	}
}
