package encryption; 

import java.io.UnsupportedEncodingException; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 
import java.util.Arrays; 
import java.util.Base64; 

import javax.crypto.Cipher; 
import javax.crypto.spec.SecretKeySpec; 

import enums.Algorithms; 
import main.EncryptionInterface; 

public  class  AESEncryption  implements EncryptionInterface {
	
	private static SecretKeySpec secretKey;

	
	private static byte[] key;

	
	private final String encryptionKey = "p:=l,]kHGv'eByu";

	
	
	public AESEncryption() {
		MessageDigest sha = null;
		try {
			key = encryptionKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	


	public String encrypt(String message) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes("UTF-8")));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	


	public String decrypt(String message) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(message)));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}

	


	public Algorithms getEncryptionType() {
		return Algorithms.AES;
	}


}
