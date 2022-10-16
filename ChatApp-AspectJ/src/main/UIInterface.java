package main;

import java.beans.PropertyChangeListener;
import org.json.JSONArray;

public interface UIInterface {
	public void start();
	
	public PropertyChangeListener getPropertyChangeListener();
	
	public void updateMessages(JSONArray messages);
}
