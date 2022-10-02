package encryption;

import encryption.Interfaces.Encryption;

public class NoneEncryption implements Encryption {

	@Override
	public String encrypt(String message) {
		return message;
	}

	@Override
	public String decrypt(String message) {
		return message;
	}

}
