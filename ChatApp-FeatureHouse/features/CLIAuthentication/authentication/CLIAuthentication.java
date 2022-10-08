package authentication;

import java.util.Scanner;

import backend.ServerConnection;
import gui.AuthenticationInterface;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CLIAuthentication implements AuthenticationInterface {

	private boolean isLoggedIn = false;
	private static boolean usesJavafx = false;

	private ServerConnection serverConnectionRef;

	public CLIAuthentication() {
	}
	
	@Override
	public void start() {
		Scanner consoleInput = new Scanner(System.in);
		while (!isLoggedIn) {
			System.out.println("username:");
			String username = consoleInput.nextLine();
			System.out.println("password:");
			String password = consoleInput.nextLine();

			if (verifyCredential(username, password)) {
				serverConnectionRef.updateMessages();
				isLoggedIn = true;
			}
			else {				
				System.out.println("username/password invalid");
			}
		}
		consoleInput.close();		
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
