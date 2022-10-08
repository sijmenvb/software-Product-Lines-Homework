package backend; 

import java.io.BufferedReader; 
import java.io.File; 
import java.io.IOException; 
import java.io.InputStreamReader; 
import java.io.PrintWriter; 
import java.net.Socket; 
import java.util.LinkedList; 
import java.util.Scanner; 

import org.json.JSONArray; 
import org.json.JSONException; 
import org.json.JSONObject; 

import encryption.NoneEncryption; //default encryption
import java.io.InputStreamReader; 
import java.io.PrintWriter; 
import java.net.Socket; 
import org.json.JSONException; 
import org.json.JSONObject; 
import enums.ActionType; 
import enums.Algorithms; 
import enums.JSONKeys; 
import enums.ResultCodes; 
import javafx.scene.paint.Color; 
import org.apache.commons.lang3.exception.ExceptionUtils; 
import javafx.scene.Scene; 
import javafx.scene.paint.Color; 
import javafx.stage.Stage; 
import main.EncryptionInterface; 
import main.PluginLoader; 
import main.LoggingInterface; 

import main.UIInterface; 

public  class  ServerConnection {
	
	static final int portNumber = 42069;

	
	private UIInterface ui;

	
	private ChatBackEnd chatBackEnd;

	
	private String token = "";

	

	private LoggingInterface logger;

	

	private String username = "";

	
	private String password = "";

	

	private EncryptionInterface encryptionClass;

	
	
	
	public ServerConnection(UIInterface ui,LoggingInterface logger) {
		this.ui = ui;
		this.chatBackEnd = new ChatBackEnd(this);

		this.logger = logger;
		//load the encryption algorithm
		File pluginFolder = new File("Plugins");
		pluginFolder.mkdir();
		LinkedList<EncryptionInterface> uiList = PluginLoader.loadClasses(pluginFolder, EncryptionInterface.class);
		if (uiList.isEmpty()) {
			encryptionClass = new NoneEncryption();//default encryption
		}else {
			encryptionClass = uiList.getFirst();
		}
		
		
	}

	
	
	public void init() {
		this.chatBackEnd.addPropertyChangeListener(ui.getPropertyChangeListener());
	}

	

	public boolean firstAuthentication(String username, String password) {
		this.username = username;// update the user name
		this.password = hash(password);// update the password

		if (Authenticate(username, this.password)) {
			if (!this.chatBackEnd.isAlive()) {// make sure the thread is not already started
				this.chatBackEnd.setDaemon(true);// close thread when program closes.
				this.chatBackEnd.start();// start listening in on the server for messages
			}
			return true;
		}
		return false;
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
	public boolean Authenticate(String username, String hashedPassword) {
		JSONObject message = new JSONObject();
		message.put(JSONKeys.ACTION_TYPE.toString(), ActionType.AUTHENTICATION.toString());
		message.put(JSONKeys.USERNAME.toString(), username);
		message.put(JSONKeys.PASSWORD.toString(), hashedPassword);


		logger.debug(this.getClass().getName(),
				String.format("User '%s' is trying to log in. Response from the server received.", username));
		JSONObject res = sendData(encrypt(message.toString()));

		try {
			if (res.getString(JSONKeys.RESULT_CODE.toString()).equals(ResultCodes.OK.toString())) {// if authentication
																									// was successful
				token = res.getString(JSONKeys.TOKEN.toString());// update the token
				logger.info(this.getClass().getName(), String.format("User '%s' logged in.", username));
				return true;
			}
		} catch (JSONException e) {
			logger.error(this.getClass().getName(),
					String.format("JSONException occured. %s", ExceptionUtils.getStackTrace(e)));
		}

		logger.error(this.getClass().getName(), "Failed login attempt.");
		return false;
	}

	

	// TODO: actually add password hashing
	private String hash(String s) {
		return s;
	}

	

	/**
	 * sends { "actionType" : "updateMessages", "token" : token, "username" :
	 * username}
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


		logger.debug(this.getClass().getName(), "Messages are tried to be updated. Response from the server received.");
		JSONObject res = sendData(encrypt(message.toString()));


		// if message sending was successful
		if (res.getString(JSONKeys.RESULT_CODE.toString()).equals(ResultCodes.OK.toString())) {
			logger.debug(this.getClass().getName(), "Messages have been updated");
			// refreshUI(res.getJSONArray(JSONKeys.MESSAGES.toString()));
			chatBackEnd.updateMessages(res.getJSONArray(JSONKeys.MESSAGES.toString()));// update all the messages
		}
		// try to reauthenticate when server returns NotAuthenticated ResultCode
		else if (res.getString(JSONKeys.RESULT_CODE.toString()).equals(ResultCodes.NotAuthenticated.toString())) {
			Authenticate(this.username, this.password);
		} else {

			logger.error(this.getClass().getName(),
					String.format("Something went wrong with messages update. Response code: %s.",
							res.getString(JSONKeys.RESULT_CODE.toString())));
		}
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
	public void sendMessage(String text, Color color) {
		JSONObject message = new JSONObject();
		message.put(JSONKeys.ACTION_TYPE.toString(), ActionType.SEND_MESSAGE.toString());
		message.put(JSONKeys.TOKEN.toString(), token);
		message.put(JSONKeys.TEXT.toString(), text);
		message.put(JSONKeys.COLOR.toString(), color.toString());
		message.put(JSONKeys.USERNAME.toString(), username);


		logger.debug(this.getClass().getName(), "Message is tried to be sent.");
		JSONObject res = sendData(encrypt(message.toString()));


		// if message sending was successful
		if (res.getString(JSONKeys.RESULT_CODE.toString()).equals(ResultCodes.OK.toString())) {
			ui.updateMessages(res.getJSONArray(JSONKeys.MESSAGES.toString()));// update all the messages

			logger.info(this.getClass().getName(),
					String.format("Message with text: '%s' send in color: '%s'.", text, color.toString()));
		} else {

			logger.error(this.getClass().getName(),
					String.format("Something went wrong with message sending. Response code: %s",
							res.getString(JSONKeys.RESULT_CODE.toString())));
		}
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

			logger.debug(this.getClass().getName(), "Socket with localhost opened.");
			PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
			out.println(data);
			logger.debug(this.getClass().getName(), "Data sent to the server. Waiting for the response.");
			// receive the reply.
			BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
			while (!in.ready()) {
			}

			logger.debug(this.getClass().getName(), "Response from the server is ready.");

			output = new JSONObject(decrypt(in.readLine()));// Read one line, decrypt and output it
			logger.debug(this.getClass().getName(), "Response received.");
			out.close();
			in.close();
			skt.close();
			logger.debug(this.getClass().getName(), "Socket, buffer in and printer our are closed.");
		} catch (JSONException e) {
			output = new JSONObject();
			output.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.JSONParseError.toString());
			logger.error(this.getClass().getName(), String.format("JSONParseError occured when trying to send data. %s",
					ExceptionUtils.getStackTrace(e)));

		} catch (Exception e) {
			output = new JSONObject();
			output.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.Failed.toString());
			logger.error(this.getClass().getName(),
					String.format("An error occured when trying to send data. %s", ExceptionUtils.getStackTrace(e)));
		}

		logger.debug(this.getClass().getName(), "Data was successfully sent");
		return output;
	}

	

	/**
	 * function that decrypts the input applying the decryption algorithm specified
	 * in json
	 * 
	 * @param encryptedMessage encrypted string
	 * @return decrypted string
	 */
	private String decrypt(String encryptedMessage) {
		JSONObject incomingJson = new JSONObject(encryptedMessage);
		String encryptionType = incomingJson.getString(JSONKeys.ENCRYPTION.toString());
		
		
		if (encryptionType.equals(Algorithms.AES.toString())) {
			logger.debug(this.getClass().getName(), "Encryption: AES.");
		} else if (encryptionType.equals(Algorithms.REVERSE.toString())){ // encryption is string reverse
			logger.debug(this.getClass().getName(), "Encryption: Reverse string.");	
		}else {
			logger.debug(this.getClass().getName(), "Encryption: None.");	
		}
		String originalMessage = encryptionClass.decrypt(incomingJson.getString(JSONKeys.ENCRYPTED_MESSAGE.toString()));
		logger.debug(this.getClass().getName(), "Data was successfully decrypted.");
		return originalMessage;
	}

	

	private String encrypt(String message) {
		JSONObject jsonForConnection = new JSONObject();

		jsonForConnection.put(JSONKeys.ENCRYPTION.toString(), encryptionClass.getEncryptionType().toString());
		
		if (encryptionClass.getEncryptionType().equals(Algorithms.AES)) {
			logger.debug(this.getClass().getName(), "Encryption: AES.");
		} else if (encryptionClass.getEncryptionType().equals(Algorithms.AES)) { // encryption is string reverse
			logger.debug(this.getClass().getName(), "Encryption: Reverse string.");
		} else {
			logger.debug(this.getClass().getName(), "Encryption: None.");
		}

		jsonForConnection.put(JSONKeys.ENCRYPTED_MESSAGE.toString(), encryptionClass.encrypt(message));
		logger.debug(this.getClass().getName(), "Data was successfully encrypted.");
		return jsonForConnection.toString();
	}


}
