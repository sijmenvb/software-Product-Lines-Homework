package backend;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.json.JSONArray;

public class ChatBackEnd extends Thread {

	private ServerConnection serverConnectionRef;
	private JSONArray currentMessages = new JSONArray();
	private PropertyChangeSupport support;	

	public ChatBackEnd(ServerConnection serverConnectionRef) {
		support = new PropertyChangeSupport(this);
		this.serverConnectionRef = serverConnectionRef;

	}
	
	private void playSound() {
		return;//play no sound by default
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
		}
	}

	public void updateMessages(JSONArray messages) {
		// check if the messages actually changed		
		if (!currentMessages.toString().equals(messages.toString())) {			
			// TODO: add user name to text.
			playSound();
			support.firePropertyChange("messages", currentMessages, messages);// notify observers that the property has changed.
			currentMessages = messages;// update current.
		}
	}

	// add PropertyChangeListener to the list of Listeners.
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}

}
