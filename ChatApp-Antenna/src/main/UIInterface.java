package main;

import java.beans.PropertyChangeListener;

import org.json.JSONArray;

import gui.AuthenticationInterface;
import javafx.stage.Stage;

public interface UIInterface {
	public boolean usesJavafx();
	
	public void javaFXStart(final Stage primaryStage, AuthenticationInterface auth);//will also set JavaFXPrimaryStage
	
	public void start(AuthenticationInterface auth);
	
	public PropertyChangeListener getPropertyChangeListener();
	
	public void updateMessages(JSONArray messages);
}
