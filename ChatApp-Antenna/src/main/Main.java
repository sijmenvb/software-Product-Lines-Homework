package main;

import backend.ServerConnection;
//#if !CLI
import gui.Authentication;
import gui.ChatWindow;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
//#endif

public class Main
//#if !CLI
		extends Application
//#endif
{

	private final long[] frameTimes = new long[100];

	private int frameTimeIndex = 0;
	private boolean arrayFilled = false;
	// #if !Authentication
	private static String anonymousUser = "anonymous";
	private static String anonymousPass = "tbAWkyi04Dt5U1XhTiFs";
	// #endif

	public static void main(String[] args) {
		// #if CLI
//@		ServerConnection serverConnection = new ServerConnection();
//@	}
	// #else
		launch(args);
		
		// AuthenticationScreen();
	}

	@Override
	public void start(final Stage primaryStage) {
		ServerConnection serverConnection = new ServerConnection(primaryStage);

		primaryStage.setTitle("Hello World!");
		// #if Authentication
//@		Authentication root = serverConnection.getAuthentication();
		// #else
		serverConnection.firstAuthentication(anonymousUser, anonymousPass);
		ChatWindow root = serverConnection.getChatWindow();
		// #endif
		primaryStage.setScene(new Scene(root, 1280, 720));
		primaryStage.show();

		// frame rate source:
		// https://stackoverflow.com/questions/28287398/what-is-the-preferred-way-of-getting-the-frame-rate-of-a-javafx-application
		AnimationTimer frameRateMeter = new AnimationTimer() {

			@Override
			public void handle(long now) {
				long oldFrameTime = frameTimes[frameTimeIndex];
				frameTimes[frameTimeIndex] = now;
				frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
				if (frameTimeIndex == 0) {
					arrayFilled = true;
				}
				if (arrayFilled) {
					long elapsedNanos = now - oldFrameTime;
					long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
					double frameRate = 1000000000.0 / elapsedNanosPerFrame;
					primaryStage.setTitle(String.format("Current frame rate: %.3f", frameRate));
				}
			}
		};

		frameRateMeter.start();
	}
	// #endif
}
