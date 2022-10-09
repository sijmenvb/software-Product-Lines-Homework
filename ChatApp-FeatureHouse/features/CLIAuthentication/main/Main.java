package main;

import authentication.CLIAuthentication;
import main.AuthenticationInterface;


public class Main {
	private static AuthenticationInterface getAuthenticator() {
		return new CLIAuthentication();
	}
}
