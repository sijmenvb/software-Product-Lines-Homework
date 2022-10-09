package backend;

import encryption.NoneEncryption;

public class ServerConnection {
	private void setEncryptionMethod() {
		encryptionClass = new NoneEncryption();//default encryption
	}
}