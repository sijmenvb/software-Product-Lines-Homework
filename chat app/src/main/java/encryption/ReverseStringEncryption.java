package encryption;

import encryption.Interfaces.Encryption;

public class ReverseStringEncryption implements Encryption {

	@Override
	public String encrypt(String message) {
		StringBuilder s_reverse = new StringBuilder(message).reverse();
		return s_reverse.toString();
	}

	@Override
	public String decrypt(String message) {
		StringBuilder s_reverse = new StringBuilder(message).reverse();
		return s_reverse.toString();
	}
}
