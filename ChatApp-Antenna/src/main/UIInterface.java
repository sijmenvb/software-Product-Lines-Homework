package main;

import java.beans.PropertyChangeListener;

import org.json.JSONArray;

import javafx.stage.Stage;

public interface UIInterface {
	public boolean usesJavafx();

	public Stage getJavaFXPrimaryStage();
	
	public void javaFXStart(final Stage primaryStage);//will also set JavaFXPrimaryStage
	
	public PropertyChangeListener getPropertyChangeListener();
	
	public void updateMessages(JSONArray messages);
}
