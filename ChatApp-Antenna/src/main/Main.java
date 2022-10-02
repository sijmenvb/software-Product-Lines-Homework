package main;

import java.io.File;
import java.util.LinkedList;

import backend.ServerConnection;
import gui.AuthenticationInterface;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logging.NullLogging;

public class Main extends Application {
	public static void main(String[] args) {
		
		File pluginFolder = new File("Plugins");
		pluginFolder.mkdir();

		LinkedList<UIInterface> uiList = PluginLoader.loadClasses(pluginFolder, UIInterface.class);
		if (uiList.isEmpty()) {
			System.err.println("APPLICATION REQUIRES UI PLUGIN!");
			return;
		}
		
		LinkedList<AuthenticationInterface> authList = PluginLoader.loadClasses(pluginFolder, AuthenticationInterface.class);
		if (authList.isEmpty()) {
			System.err.println("APPLICATION REQUIRES AUTH PLUGIN!");
			return;
		LinkedList<LoggingInterface> loggersList = PluginLoader.loadClasses(pluginFolder, LoggingInterface.class);
		LoggingInterface logger = null;
		if(loggersList.isEmpty()) {
			logger = new NullLogging();
		} else {
			logger = loggersList.getFirst();
			logger.Init();
		}

		UIInterface ui = uiList.getFirst();
		AuthenticationInterface auth = authList.getFirst();
		
		if (ui.usesJavafx()) {
			launch(args);
		} else {
			ui.start(auth, logger);
		}
	}

	@Override
	public void start(final Stage primaryStage) {
		//unfortunately we have to load the ui again since we can not pass it in a static context.
		File pluginFolder = new File("Plugins");
		pluginFolder.mkdir();

		LinkedList<UIInterface> uiList = PluginLoader.loadClasses(pluginFolder, UIInterface.class);
		if (uiList.isEmpty()) {
			System.err.println("APPLICATION REQUIRES UI PLUGIN!");
			return;
		}
		
		LinkedList<AuthenticationInterface> authList = PluginLoader.loadClasses(pluginFolder, AuthenticationInterface.class);
		if (authList.isEmpty()) {
			System.err.println("APPLICATION REQUIRES AUTH PLUGIN!");
			return;
		}
		LinkedList<LoggingInterface> loggersList = PluginLoader.loadClasses(pluginFolder, LoggingInterface.class);
		LoggingInterface logger = null;
		if(loggersList.isEmpty()) {
			logger = new NullLogging();
		} else {
			logger = loggersList.getFirst();
			logger.Init();
		}


		UIInterface ui = uiList.getFirst();
		AuthenticationInterface auth = authList.getFirst();

		ui.javaFXStart(primaryStage, auth, logger);
	}
}
