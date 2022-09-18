package backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import encryption.AESEncryption;
import encryption.ReverseStringEncryption;
import encryption.Interfaces.Encryption;
import enums.ActionType;
import enums.Algorithms;
import enums.JSONKeys;
import enums.ResultCodes;
import gui.Authentication;
import gui.ChatWindow;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ServerConnection {
	static final int portNumber = 42069;
	private ChatWindow chatWindow;
	private Authentication authentication;
	private String token = "";
	private final String jsonEncryptionKey = "p:=l,]kHGv'eByu";
	
	static Logger log = Logger.getLogger(ChatWindow.class.getName()); 

	private String username = "";
	public ServerConnection(Stage primaryStage) {
		this.chatWindow = new ChatWindow(this);
		this.authentication = new Authentication(primaryStage, new Scene(chatWindow, 1280, 720), this);
	}

	/**
	 * sends { "actionType" : "authentication", "username" : username, "password" :
	 * password}
	 * 
	 * expects { "resultCode" : "ok", "token" : new token}
	 * 
	 * will save the new token for further communication. returns true if
	 * authenticated, false otherwise.
	 */
	public boolean Authenticate(String username, String password) {
		JSONObject message = new JSONObject();
		message.put(JSONKeys.ACTION_TYPE.toString(), ActionType.AUTHENTICATION.toString());
		message.put(JSONKeys.USERNAME.toString(), username);
		message.put(JSONKeys.PASSWORD.toString(), hash(password));

		JSONObject res = sendData(encrypt(message.toString(), Algorithms.AES));

		System.out.println(res);
		// if authentication was successful
		if (res.getString(JSONKeys.RESULT_CODE.toString()).equals(ResultCodes.OK.toString())) {
			token = res.getString(JSONKeys.TOKEN.toString());// update the token
			log.info("user logged in");
			this.username = username;//update the user name
			return true;
		}
		log.info("failed login attempt");
		return false;
	}

	// TODO: actually add password hashing
	private String hash(String s) {
		return s;
	}

	/**
	 * sends { "actionType" : "updateMessages", "token" : token, "username" : username}
	 * 
	 * expects { "resultCode" : "ok", "messages" : array_with_messages} where
	 * array_with_messages is
	 * [{"text":text1,"color":color1},{"text":text2,"color":color2},etc.] where
	 * text1 and color1 are stings as send using sendMessage()
	 * 
	 * 
	 */
	public void updateMessages() {
		JSONObject message = new JSONObject();
		message.put(JSONKeys.ACTION_TYPE.toString(), ActionType.UPDATE_MESSAGES.toString());
		message.put(JSONKeys.TOKEN.toString(), token);
		message.put(JSONKeys.USERNAME.toString(), username);

		JSONObject res = sendData(encrypt(message.toString(), Algorithms.AES));

		// if message sending was successful
		if (res.getString(JSONKeys.RESULT_CODE.toString()).equals(ResultCodes.OK.toString())) {
			chatWindow.updateMessages(res.getJSONArray(JSONKeys.MESSAGES.toString()));// update all the messages
		}
		log.info("messages updated");
	}

	/**
	 * sends { "actionType" : "sendMessage", "token" : token, "text" : text, "color"
	 * : color,"username" : username}
	 * 
	 * expects { "resultCode" : "ok", "messages" : array_with_messages}
	 * 
	 * where array_with_messages is [{"text":text1 , "color" : color1}, {"text" :
	 * text2,"color" : color2},etc.] where text1 and color1 are stings as send using
	 * sendMessage()
	 * 
	 */
	public void sendMessage(String text, Color color, Algorithms encryptionAlg) {
		JSONObject message = new JSONObject();
		message.put(JSONKeys.ACTION_TYPE.toString(), ActionType.SEND_MESSAGE.toString());
		message.put(JSONKeys.TOKEN.toString(), token);
		message.put(JSONKeys.TEXT.toString(), text);
		message.put(JSONKeys.COLOR.toString(), color.toString());
		message.put(JSONKeys.USERNAME.toString(), username);

		
		JSONObject res = sendData(encrypt(message.toString(), encryptionAlg));

		// if message sending was successful
		if (res.getString(JSONKeys.RESULT_CODE.toString()).equals(ResultCodes.OK.toString())) {
			chatWindow.updateMessages(res.getJSONArray(JSONKeys.MESSAGES.toString()));// update all the messages
		}
		log.info("message with text:\n" + text + "\nsend in color:'\n" + color.toString());
	}

	/**
	 * sends the data to the server and returns the result as a JSONObject.
	 * 
	 */

	private JSONObject sendData(String data) {
		JSONObject output;
		try {
			Socket skt = new Socket("localhost", portNumber);
			// send the data
			PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
			out.println(data);

			// receive the reply.
			BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
			while (!in.ready()) {
			}
 
			output = new JSONObject(decrypt(in.readLine()));// Read one line, decrypt and output it
			out.close();
			in.close();
			skt.close();
		} catch (JSONException e) {
			output = new JSONObject();
			output.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.JSONParseError.toString());
			log.debug("JSONParseError occured when trying to send data");

		} catch (Exception e) {
			output = new JSONObject();
			output.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.Failed.toString());
			log.info("an error occured when trying to send data");
		}

		log.info("data was successfully sent");
		return output;
	}

	public ChatWindow getChatWindow() {
		return chatWindow;
	}

	public Authentication getAuthentication() {
		return authentication;
	}
	
	/**
	 * function that decrypts the input applying the decryption algorithm specified in json
	 * 
	 * @param s encrypted string
	 * @return decrypted string
	 */
	private String decrypt(String encryptedMessage) {
		JSONObject incomingJson = new JSONObject(encryptedMessage);
		String encryptionType = incomingJson.getString(JSONKeys.ENCRYPTION.toString());
		Encryption encryptionClass;
		
		if(encryptionType.equals(Algorithms.AES.toString())) {
			encryptionClass = new AESEncryption(jsonEncryptionKey);
		} else {
			encryptionClass = new ReverseStringEncryption();
		}
		String originalMessage = encryptionClass.decrypt(incomingJson.getString(JSONKeys.ENCRYPTED_MESSAGE.toString()));
		return originalMessage;
	}
	
	private String encrypt(String message, Algorithms encryptionAlg) {
		JSONObject jsonForConnection = new JSONObject();
		jsonForConnection.put(JSONKeys.ENCRYPTION.toString(), encryptionAlg.toString());
		Encryption encryptionClass;
		
		if(encryptionAlg.equals(Algorithms.AES)) {
			encryptionClass = new AESEncryption(jsonEncryptionKey);
		} else {
			encryptionClass = new ReverseStringEncryption();
		}
		jsonForConnection.put(JSONKeys.ENCRYPTED_MESSAGE.toString(), encryptionClass.encrypt(message));
		return jsonForConnection.toString();
	}

}
