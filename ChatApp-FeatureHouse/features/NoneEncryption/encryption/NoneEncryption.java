package encryption;
import enums.Algorithms;
import main.EncryptionInterface;

public class NoneEncryption implements EncryptionInterface {


	public String encrypt(String message) {
		return message;
	}


	public String decrypt(String message) {
		return message;
	}


	public Algorithms getEncryptionType() {
		return Algorithms.None;
	}

}
