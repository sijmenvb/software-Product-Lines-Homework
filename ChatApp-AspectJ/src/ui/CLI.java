package ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.json.JSONArray;
import org.json.JSONObject;

import backend.ServerConnection;
import enums.JSONKeys;
import main.UIInterface;

public class CLI implements UIInterface, PropertyChangeListener {
	

	private static String noAuthUsername = "noauth";
	private static String noAuthPassword = "noauth";
	
	public boolean isLoggedIn = false;
	private ServerConnection serverConnectionRef;
	
	public void start() {
		serverConnectionRef = new ServerConnection(this);
		serverConnectionRef.init();
		isLoggedIn = authenticate();
		if(isLoggedIn) {
			serverConnectionRef.updateMessages();
		}else {
			System.out.println("Couldn't log in!");
		}
	}
	
	public boolean authenticate() {
		return verifyCredential(noAuthUsername, noAuthPassword);
	}
	
	public boolean verifyCredential(String username, String password) {
		return serverConnectionRef.firstAuthentication(username, password);
	}

	private void refreshUI(JSONArray messages) {
		System.out.println("\n\n\n\n\n\n");// clear the console
		for (Object object : messages) {
			if (object instanceof JSONObject) {
				JSONObject textObject = (JSONObject) object;// cast to jsonObject
				printMessage(textObject);
				
			}
		}
	}
	
	private void printMessage(JSONObject textObject) {
		String message = textObject.getString(JSONKeys.TEXT.toString());
		System.out.print(message);
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
