import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;

import java.util.Arrays;

import backend.ServerConnection;
import enums.Algorithms;
import enums.JSONKeys;

public aspect AESEncryption {
	
	private static SecretKeySpec secretKey;
	private static byte[] key;
	private final String encryptionKey = "p:=l,]kHGv'eByu";
	
	pointcut encryptMessage(ServerConnection sc, String s) : 
		execution(String *.encrypt(String)) && args(s) && target(sc);
	
	String around(ServerConnection sc, String s): encryptMessage(sc, s) {
		if(key == null || secretKey == null) {
			init();
		}
		String encryptedMessage = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			encryptedMessage = Base64.getEncoder().encodeToString(cipher.doFinal(s.getBytes("UTF-8")));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
			return encryptedMessage;
		}

		JSONObject jsonForConnection = new JSONObject();
		jsonForConnection.put(JSONKeys.ENCRYPTION.toString(), Algorithms.AES.toString());
		jsonForConnection.put(JSONKeys.ENCRYPTED_MESSAGE.toString(), encryptedMessage);
		return jsonForConnection.toString();
	}
	
	private void init() {
		MessageDigest sha = null;
		try {
			key = encryptionKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
