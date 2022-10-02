package gui;

import java.beans.PropertyChangeListener;

import org.json.JSONArray;

import backend.ServerConnection;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.LoggingInterface;
import main.UIInterface;

public class GUI implements UIInterface {
	
	private final long[] frameTimes = new long[100];
	private int frameTimeIndex = 0;
	private boolean arrayFilled = false;
	
	private ChatWindow chatWindow;
	private Authentication authentication;
	
	
	Stage JavaFXPrimaryStage;
	@Override
	public boolean usesJavafx() {
		return true;
	}

	@Override
	public void javaFXStart(Stage primaryStage, LoggingInterface logger) {
		JavaFXPrimaryStage = primaryStage;
		ServerConnection serverConnection = new ServerConnection(this, logger);
		
		this.chatWindow = new ChatWindow(serverConnection, logger);
		
		primaryStage.setTitle("AmazingChatApp!");
		// #if Authentication
		this.authentication = new Authentication(primaryStage, new Scene(chatWindow, 1280, 720), serverConnection, logger);
		Authentication root = authentication;
		// #else
//@		serverConnection.firstAuthentication("admin", "admin");
//@		ChatWindow root = this.chatWindow;
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
	
	@Override
	public PropertyChangeListener getPropertyChangeListener() {
		return chatWindow;
	}

	@Override
	public void updateMessages(JSONArray messages) {
		chatWindow.updateMessages(messages);
	}

	@Override
	public void start(LoggingInterface logger) {
		throw new UnsupportedOperationException("this interface uses JavaFX use javaFXStart() instead");
		
	}
	

}
