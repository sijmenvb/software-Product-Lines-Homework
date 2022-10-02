package main;

import java.io.File;
import java.util.LinkedList;

import backend.ServerConnection;
import logging.NullLogging;

import org.apache.log4j.Logger;

//#if !CLI
//@import gui.Authentication;
//@import gui.ChatWindow;
//@import javafx.animation.AnimationTimer;
//@import javafx.application.Application;
//@import javafx.scene.Scene;
//@import javafx.stage.Stage;
//#endif

public class Main
//#if !CLI
//@		extends Application
//#endif
{

	private final long[] frameTimes = new long[100];
	private int frameTimeIndex = 0;
	private boolean arrayFilled = false;

	public static void main(String[] args) {		
		//#if CLI
		File pluginFolder = new File("Plugins");
		pluginFolder.mkdir();// create plugins folder in current location.
		
		//load the plugins that implement MyInterface
		LinkedList<ILogging> loggersList = PluginLoader.loadClasses(pluginFolder, ILogging.class);
		
		ILogging logger = null;
		
		if(loggersList.isEmpty()) {
			logger = new NullLogging();
		} else {
			logger = loggersList.getFirst();
			logger.Init();
		}
		ServerConnection serverConnection = new ServerConnection(logger);
	}
	// #else
//@		launch(args);
//@		
//@		// AuthenticationScreen();
//@	}
//@
//@	@Override
//@	public void start(final Stage primaryStage) {
//@		File pluginFolder = new File("Plugins");
//@		pluginFolder.mkdir();// create plugins folder in current location.
//@		
//@		LinkedList<ILogging> loggersList = PluginLoader.loadClasses(pluginFolder, ILogging.class);
//@		
//@		ILogging logger = null;
//@		if(loggersList.isEmpty()) {
//@			logger = new NullLogging();
//@		} else {
//@			logger = loggersList.getFirst();
//@			logger.Init();
//@		}
//@		
//@		ServerConnection serverConnection = new ServerConnection(primaryStage, logger);
//@
//@		primaryStage.setTitle("Hello World!");
	// #if Authentication
//@		Authentication root = serverConnection.getAuthentication();
	// #else
//@		serverConnection.firstAuthentication("admin", "admin");
//@		ChatWindow root = serverConnection.getChatWindow();
	// #endif
//@		primaryStage.setScene(new Scene(root, 1280, 720));
//@		primaryStage.show();
//@
//@		// frame rate source:
//@		// https://stackoverflow.com/questions/28287398/what-is-the-preferred-way-of-getting-the-frame-rate-of-a-javafx-application
//@		AnimationTimer frameRateMeter = new AnimationTimer() {
//@
//@			@Override
//@			public void handle(long now) {
//@				long oldFrameTime = frameTimes[frameTimeIndex];
//@				frameTimes[frameTimeIndex] = now;
//@				frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
//@				if (frameTimeIndex == 0) {
//@					arrayFilled = true;
//@				}
//@				if (arrayFilled) {
//@					long elapsedNanos = now - oldFrameTime;
//@					long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
//@					double frameRate = 1000000000.0 / elapsedNanosPerFrame;
//@					primaryStage.setTitle(String.format("Current frame rate: %.3f", frameRate));
//@				}
//@			}
//@		};
//@
//@		frameRateMeter.start();
//@	}
	// #endif
}
