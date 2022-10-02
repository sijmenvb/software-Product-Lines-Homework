package cli;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import backend.ServerConnection;
import enums.JSONKeys;
import javafx.stage.Stage;
import main.LoggingInterface;
import main.UIInterface;

public class CLI implements UIInterface, PropertyChangeListener {

	@Override
	public void start(LoggingInterface logger) {
		ServerConnection serverConnection = new ServerConnection(this, logger);

		Scanner consoleInput = new Scanner(System.in);
		// #if Authentication
//@		System.out.println("username:");
//@		String username = consoleInput.nextLine();
//@		System.out.println("password:");
//@		String password = consoleInput.nextLine();
//@		serverConnection.firstAuthentication(username, password);
		// #else
		 serverConnection.firstAuthentication("admin", "admin");
		// #endif
		serverConnection.updateMessages();

		consoleInput.nextLine();
		consoleInput.close();
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

	@Override
	public boolean usesJavafx() {
		return false;
	}

	@Override
	public void javaFXStart(Stage primaryStage, LoggingInterface logger) {
		throw new UnsupportedOperationException("this interface does not use JavaFX");
	}

	@Override
	public PropertyChangeListener getPropertyChangeListener() {
		return this;
	}

	@Override
	public void updateMessages(JSONArray messages) {
		refreshUI(messages);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("event name:" + evt.getPropertyName());
		refreshUI((JSONArray) evt.getNewValue());
	}

}
