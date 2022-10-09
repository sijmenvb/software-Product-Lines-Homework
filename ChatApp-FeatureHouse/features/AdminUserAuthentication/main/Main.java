package main;

import authentication.AdminUserAuthentication;
import main.AuthenticationInterface;


public class Main {
	private static AuthenticationInterface getAuthenticator() {
		return new AdminUserAuthentication();
	}
}
