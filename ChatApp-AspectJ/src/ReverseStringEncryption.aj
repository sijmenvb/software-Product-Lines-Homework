import org.json.JSONObject;
import enums.Algorithms;
import enums.JSONKeys;
import backend.ServerConnection;

public aspect ReverseStringEncryption {
	pointcut encryptMessage(ServerConnection sc, String s) : 
		execution(String *.encrypt(String)) && args(s) && target(sc);
	
	String around(ServerConnection sc, String s): encryptMessage(sc, s) {
		String reversed = new StringBuilder(s).reverse().toString();
		JSONObject jsonForConnection = new JSONObject();
		jsonForConnection.put(JSONKeys.ENCRYPTION.toString(), Algorithms.REVERSE.toString());
		jsonForConnection.put(JSONKeys.ENCRYPTED_MESSAGE.toString(), reversed);
		return jsonForConnection.toString();
	}
}
