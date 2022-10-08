package encryption;

import enums.Algorithms;
import main.EncryptionInterface;

public class ReverseStringEncryption implements EncryptionInterface {

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

	@Override
	public Algorithms getEncryptionType() {
		return Algorithms.REVERSE;
	}
}
