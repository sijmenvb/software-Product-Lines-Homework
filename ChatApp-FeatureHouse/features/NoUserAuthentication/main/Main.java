package main;

import authentication.NoUserAuthentication;
import main.AuthenticationInterface;
import logging.NullLogging;

public class Main {
	private static AuthenticationInterface getAuthenticator() {
		return new NoUserAuthentication();
	}
}
