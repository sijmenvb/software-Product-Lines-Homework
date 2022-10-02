package authentication_plugins;

import backend.ServerConnection;
import gui.AuthenticationInterface;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NoUserAuthentication implements AuthenticationInterface {

	private static String noAuthUsername = "noauth";
	private static String noAuthPassword = "noauth";
	
	private boolean isLoggedIn = false;
	private static boolean isJafaFX = false;
	
	private ServerConnection serverConnectionRef;
	
	public NoUserAuthentication(ServerConnection serverConnection) {
		
		serverConnectionRef = serverConnection;
		
		if (verifyCredential(noAuthUsername, noAuthPassword)) {
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
	public boolean isJafaFX() {
		return isJafaFX;
	}

}
