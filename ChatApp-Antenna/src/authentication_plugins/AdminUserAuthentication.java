package authentication_plugins;

import backend.ServerConnection;
import gui.AuthenticationInterface;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdminUserAuthentication implements AuthenticationInterface {

	private static String adminUsername = "admin";
	private static String adminPassword = "admin";
	
	private boolean isLoggedIn = false;
	private static boolean isJafaFX = false;
	
	private ServerConnection serverConnectionRef;
	
	public AdminUserAuthentication(ServerConnection serverConnection) {
		
		serverConnectionRef = serverConnection;
		
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
	public boolean isJafaFX() {
		return isJafaFX;
	}

}
