package backend;

import encryption.AESEncryption;

public class ServerConnection {
	private void setEncryptionMethod() {
		encryptionClass = new AESEncryption();//default encryption
	}
}