package gui; 

import java.beans.PropertyChangeListener; 

import org.json.JSONArray; 
import backend.ServerConnection; 
import javafx.animation.AnimationTimer; 
import javafx.scene.Parent; 
import javafx.scene.Scene; 
import javafx.stage.Stage; 
import main.LoggingInterface; 
import main.UIInterface; 

public  class  GUI  implements UIInterface {
	

	private final long[] frameTimes = new long[100];

	
	private int frameTimeIndex = 0;

	
	private boolean arrayFilled = false;

	

	private ChatWindow chatWindow;

	
	// private AuthenticationInterface authentication;

	Stage JavaFXPrimaryStage;

	

	@Override
	public boolean usesJavafx() {
		return true;
	}

	

	@Override
	public void javaFXStart(Stage primaryStage, AuthenticationInterface auth, LoggingInterface logger) {
		JavaFXPrimaryStage = primaryStage;
		ServerConnection serverConnection = new ServerConnection(this, logger);

		this.chatWindow = new ChatWindow(serverConnection, logger);
		serverConnection.init();

		auth.setServerConnection(serverConnection);

		primaryStage.setTitle("Hello World!");
		if (auth.usesJavafx()) {
			auth.setPrimaryStage(primaryStage);
			auth.setNextScene(new Scene(this.chatWindow, 1280, 720));
			auth.start();
			primaryStage.setScene(new Scene((Parent) auth, 1280, 720));
		} else {
			auth.start();
			if (auth.userIsLoggedIn()) {
				primaryStage.setScene(new Scene(this.chatWindow, 1280, 720));
			}
		}

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
		return this.chatWindow;
	}

	

	@Override
	public void updateMessages(JSONArray messages) {
		chatWindow.updateMessages(messages);
	}

	

	@Override
	public void start(AuthenticationInterface auth, LoggingInterface logger) {
		throw new UnsupportedOperationException("this interface uses JavaFX use javaFXStart() instead");

	}


}
