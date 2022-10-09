package main;

import authentication.NoUserAuthentication;
import gui.AuthenticationInterface;

public class Main extends Application {
	private AuthenticationInterface getAuthenticator() {
		return new NoUserAuthentication();
	}
}
