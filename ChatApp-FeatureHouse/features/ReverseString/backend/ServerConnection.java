package backend;

import encryption.ReverseStringEncryption;

public class ServerConnection {
	private void setEncryptionMethod() {
		encryptionClass = new ReverseStringEncryption();//default encryption
	}
}