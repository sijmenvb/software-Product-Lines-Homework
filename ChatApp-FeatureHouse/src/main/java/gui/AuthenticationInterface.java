package gui; 

import backend.ServerConnection; 
import javafx.scene.Scene; 
import javafx.stage.Stage; 

public  interface  AuthenticationInterface {
	
	
	void start();

	
	
	void setServerConnection(ServerConnection serverConnection);

	
	
	void setPrimaryStage(Stage primaryStage);

	
	
	void setNextScene(Scene nextScene);

	
	
	boolean verifyCredential(String username, String password);

	
	
	boolean userIsLoggedIn();

	
	
	boolean usesJavafx();


}
