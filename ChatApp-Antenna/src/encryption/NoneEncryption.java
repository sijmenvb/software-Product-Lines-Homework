package encryption;
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

}
