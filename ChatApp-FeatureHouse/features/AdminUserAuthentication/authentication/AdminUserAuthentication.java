package authentication;

import backend.ServerConnection;
import main.AuthenticationInterface;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdminUserAuthentication implements AuthenticationInterface {

	private static String adminUsername = "admin";
	private static String adminPassword = "admin";

	private boolean isLoggedIn = false;
	private static boolean usesJavafx = false;

	private ServerConnection serverConnectionRef;

	public AdminUserAuthentication() {

	}

	public void start() {
		if (verifyCredential(adminUsername, adminPassword)) {
			serverConnectionRef.updateMessages();
			isLoggedIn = true;
		}
	}


	public boolean verifyCredential(String username, String password) {
		return serverConnectionRef.firstAuthentication(username, password);
	}


	public boolean userIsLoggedIn() {
		return isLoggedIn;
	}


	public boolean usesJavafx() {
		return usesJavafx;
	}


	public void setServerConnection(ServerConnection serverConnection) {
		serverConnectionRef = serverConnection;
	}


	public void setPrimaryStage(Stage primaryStage) {
		throw new UnsupportedOperationException("this interface does not use JavaFX");
	}


	public void setNextScene(Scene nextScene) {
		throw new UnsupportedOperationException("this interface does not use JavaFX");
	}
}
