package backend;

import encryption.NoneEncryption;

/**
 * TODO description
 */
public class ServerConnection {
	private void setEncryptionMethod() {
		encryptionClass = new NoneEncryption();//default encryption
	}
}