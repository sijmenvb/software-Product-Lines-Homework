package main;

import authentication.GUIAuthentication;
import main.AuthenticationInterface;


public class Main {
	
	private static AuthenticationInterface getAuthenticator() {
		return new GUIAuthentication();
	}
}
