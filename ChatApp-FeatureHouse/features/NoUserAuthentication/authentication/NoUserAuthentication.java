package authentication;

import backend.ServerConnection;
import main.AuthenticationInterface;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NoUserAuthentication implements AuthenticationInterface {

	private static String noAuthUsername = "noauth";
	private static String noAuthPassword = "noauth";

	private boolean isLoggedIn = false;
	private static boolean usesJavafx = false;

	private ServerConnection serverConnectionRef;

	public NoUserAuthentication() {

	}

	public void start() {
		if (verifyCredential(noAuthUsername, noAuthPassword)) {
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
