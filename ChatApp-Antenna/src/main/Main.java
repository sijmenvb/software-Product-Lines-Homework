package main;

import backend.ServerConnection;
import logger.Logging;
import logger.interfaces.ILogging;

//#if !CLI
//@import gui.Authentication;
//@import gui.ChatWindow;
//@import javafx.animation.AnimationTimer;
//@import javafx.application.Application;
//@import javafx.scene.Scene;
//@import javafx.stage.Stage;
//@import logger.Logging;
//@import logger.interfaces.ILogger;
//#endif

public class Main
//#if !CLI
//@		extends Application
//#endif
{

	private final long[] frameTimes = new long[100];
	private ILogging logger;

	private int frameTimeIndex = 0;
	private boolean arrayFilled = false;

	public static void main(String[] args) {
		// #if CLI
		ServerConnection serverConnection = new ServerConnection(new Logging());
	}
	// #else
//@		launch(args);
//@		
//@		// AuthenticationScreen();
//@	}
//@
//@	@Override
//@	public void start(final Stage primaryStage) {
//@		ServerConnection serverConnection = new ServerConnection(primaryStage, new Logging());
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
