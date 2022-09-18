package backend;

import java.util.List;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import gui.ChatWindow;
import javafx.application.Platform;
import javafx.scene.paint.Color;

public class ChatBackEnd extends Thread {

	private ServerConnection serverConnectionRef;
	private JSONArray currentMessages = new JSONArray();
	private PropertyChangeSupport support;	

	public ChatBackEnd(ServerConnection serverConnectionRef) {
		support = new PropertyChangeSupport(this);
		this.serverConnectionRef = serverConnectionRef;

	}

	@Override
	public void run() {
		while (true) {
			serverConnectionRef.updateMessages();
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("this is the back-end thread");
		}
	}

	public void updateMessages(JSONArray messages) {
		// check if the messages actually changed		
		if (!currentMessages.toString().equals(messages.toString())) {			
			// TODO: add user name to text.
			support.firePropertyChange("messages", currentMessages, messages);// notify observers that the property has changed.
			currentMessages = messages;// update current.
		}
	}

	// add PropertyChangeListener to the list of Listeners.
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}

}
