package main;

import java.beans.PropertyChangeListener;

import org.json.JSONArray;

import javafx.stage.Stage;

public interface UIInterface {
	public boolean usesJavafx();
	
	public void javaFXStart(final Stage primaryStage, LoggingInterface logger);//will also set JavaFXPrimaryStage
	
	public void start(LoggingInterface logging);
	
	public PropertyChangeListener getPropertyChangeListener();
	
	public void updateMessages(JSONArray messages);
}
