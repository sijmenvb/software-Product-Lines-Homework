package authentication_plugins;

import backend.ServerConnection;
import gui.AuthenticationInterface;
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

	@Override
	public void start() {
		if (verifyCredential(adminUsername, adminPassword)) {
			serverConnectionRef.updateMessages();
			isLoggedIn = true;
		}
	}

	@Override
	public boolean verifyCredential(String username, String password) {
		return serverConnectionRef.firstAuthentication(username, password);
	}

	@Override
	public boolean userIsLoggedIn() {
		return isLoggedIn;
	}

	@Override
	public boolean usesJavafx() {
		return usesJavafx;
	}

	@Override
	public void setServerConnection(ServerConnection serverConnection) {
		serverConnectionRef = serverConnection;
	}

	@Override
	public void setPrimaryStage(Stage primaryStage) {
		throw new UnsupportedOperationException("this interface does not use JavaFX");
	}

	@Override
	public void setNextScene(Scene nextScene) {
		throw new UnsupportedOperationException("this interface does not use JavaFX");
	}

}
