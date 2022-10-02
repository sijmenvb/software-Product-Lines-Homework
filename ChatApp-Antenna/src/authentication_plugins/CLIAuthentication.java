package authentication_plugins;

import java.util.Scanner;

import backend.ServerConnection;
import gui.AuthenticationInterface;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CLIAuthentication implements AuthenticationInterface {

	private boolean isLoggedIn = false;
	private static boolean isJafaFX = false;

	private ServerConnection serverConnectionRef;

	public CLIAuthentication(ServerConnection serverConnection) {

		serverConnectionRef = serverConnection;
		Scanner consoleInput = new Scanner(System.in);

		while (!isLoggedIn) {
			System.out.println("username:");
			String username = consoleInput.nextLine();
			System.out.println("password:");
			String password = consoleInput.nextLine();
			consoleInput.nextLine();

			if (verifyCredential(username, password)) {
				serverConnectionRef.updateMessages();
				isLoggedIn = true;
			}
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
