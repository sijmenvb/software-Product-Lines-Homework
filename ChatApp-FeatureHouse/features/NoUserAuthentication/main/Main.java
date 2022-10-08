package main;

import authentication.NoUserAuthentication;
import gui.AuthenticationInterface;
import logging.NullLogging;

public class Main extends Application {
	private AuthenticationInterface getAuthenticator() {
		return new NoUserAuthentication();
	}
}
