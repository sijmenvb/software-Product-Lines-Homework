package encryption;
import enums.Algorithms;
import main.EncryptionInterface;

public class NoneEncryption implements EncryptionInterface {

	@Override
	public String encrypt(String message) {
		return message;
	}

	@Override
	public String decrypt(String message) {
		return message;
	}

	@Override
	public Algorithms getEncryptionType() {
		return Algorithms.None;
	}

}
