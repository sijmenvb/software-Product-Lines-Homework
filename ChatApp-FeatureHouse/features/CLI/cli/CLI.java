package cli;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import backend.ServerConnection;
import enums.JSONKeys;
import main.AuthenticationInterface;
import javafx.stage.Stage;
import main.LoggingInterface;
import main.UIInterface;

public class CLI implements UIInterface, PropertyChangeListener {


	public void start(AuthenticationInterface auth, LoggingInterface logger) {
		ServerConnection serverConnection = new ServerConnection(this, logger);
		serverConnection.init();
		
		auth.setServerConnection(serverConnection);
		auth.start();
	}

	// TODO: make the update actually work!

	private void refreshUI(JSONArray messages) {
		System.out.println("\n\n\n\n\n\n");// clear the console
		for (Object object : messages) {
			if (object instanceof JSONObject) {
				JSONObject textObject = (JSONObject) object;// cast to jsonObject
				System.out.print(textObject.getString(JSONKeys.TEXT.toString()));
			}
		}
	}


	public boolean usesJavafx() {
		return false;
	}


	public void javaFXStart(Stage primaryStage, AuthenticationInterface auth, LoggingInterface logger) {
		throw new UnsupportedOperationException("this interface does not use JavaFX");
	}


	public PropertyChangeListener getPropertyChangeListener() {
		return this;
	}


	public void updateMessages(JSONArray messages) {
		refreshUI(messages);
	}


	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("event name:" + evt.getPropertyName());
		refreshUI((JSONArray) evt.getNewValue());
	}

}