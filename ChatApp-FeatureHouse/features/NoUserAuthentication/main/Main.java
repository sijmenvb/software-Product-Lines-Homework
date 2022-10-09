package main;

import authentication.NoUserAuthentication;
import main.AuthenticationInterface;


public class Main {
	private static AuthenticationInterface getAuthenticator() {
		return new NoUserAuthentication();
	}
}
