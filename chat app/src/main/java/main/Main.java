package main;

import backend.ServerConnection;
import gui.ChatWindow;
import gui.Authentication;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

	private final long[] frameTimes = new long[100];

	private int frameTimeIndex = 0;
	private boolean arrayFilled = false;

	public static void main(String[] args) {
		launch(args);
		// AuthenticationScreen();
	}

	@Override
	public void start(final Stage primaryStage) {
		ServerConnection serverConnection = new ServerConnection(primaryStage);

		primaryStage.setTitle("Hello World!");
		Authentication root = serverConnection.getAuthentication();
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

}