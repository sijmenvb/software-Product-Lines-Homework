package gui;

public interface AuthenticationInterface {
	
	boolean verifyCredential(String username, String password);
	
	boolean userIsLoggedIn();
	
	boolean isJafaFX();
}
