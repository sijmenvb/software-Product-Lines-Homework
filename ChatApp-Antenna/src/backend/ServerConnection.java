package backend;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//#if Logging
import org.apache.commons.lang3.exception.ExceptionUtils;
//#endif

import encryption.AESEncryption;
import encryption.ReverseStringEncryption;
import encryption.Interfaces.Encryption;
import enums.ActionType;
import enums.Algorithms;
import enums.JSONKeys;
import enums.ResultCodes;
import javafx.scene.paint.Color;
import logger.interfaces.ILogging;

//#if !CLI
//@import gui.Authentication;
//@import gui.ChatWindow;
//@import javafx.scene.Scene;
//@import javafx.scene.paint.Color;
//@import javafx.stage.Stage;
//#endif

public class ServerConnection
//#if CLI
		implements PropertyChangeListener
//#endif
{
	static final int portNumber = 42069;
	// #if !CLI
//@	private ChatWindow chatWindow;
	// #if Authentication
//@	private Authentication authentication;
	// #endif
	// #endif
	private ChatBackEnd chatBackEnd;
	private String token = "";

	// #if Logging
	private ILogging logger;
	// #endif

	private String username = "";
	private String password = "";
	private final String jsonEncryptionKey = "p:=l,]kHGv'eByu";

	// #if CLI
	public ServerConnection(ILogging loggerger) {
		this.chatBackEnd = new ChatBackEnd(this);
		this.chatBackEnd.addPropertyChangeListener(this);
		Scanner consoleInput = new Scanner(System.in);
		// #if Authentication
		System.out.println("username:");
		String username = consoleInput.nextLine();
		System.out.println("password:");
		String password = consoleInput.nextLine();
		firstAuthentication(username, password);
		// #else
//@		firstAuthentication("admin", "admin");
		// #endif
		updateMessages();

		
		consoleInput.nextLine();
	}
	
	//TODO: make the update actually work!
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("event name:"+evt.getPropertyName());
		refreshUI((JSONArray) evt.getNewValue());
	}

	private void refreshUI(JSONArray messages) {
		System.out.println("\n\n\n\n\n\n");//clear the console
		for (Object object : messages) {
			if (object instanceof JSONObject) {
				JSONObject textObject = (JSONObject) object;// cast to jsonObject
				System.out.print(textObject.getString(JSONKeys.TEXT.toString()));
			}
		}
	}
	// #else
//@	public ServerConnection(Stage primaryStage, ILogger logger) {
//@		this.logger = logger;
//@		this.chatWindow = new ChatWindow(this, this.logger);
//@		this.chatBackEnd = new ChatBackEnd(this);
//@		this.chatBackEnd.addPropertyChangeListener(chatWindow);
		// #if Authentication
//@		this.authentication = new Authentication(primaryStage, new Scene(chatWindow, 1280, 720), this, this.logger);
		// #endif
//@	}
	// #endif

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

		// #if Logging
		logger.debug(this.getClass().getName(), String.format("User '%s' is trying to logger in. Response from the server received.", username));
		// #endif
		JSONObject res = sendData(encrypt(message.toString(), Algorithms.AES));

		try {
			if (res.getString(JSONKeys.RESULT_CODE.toString()).equals(ResultCodes.OK.toString())) {// if authentication
																									// was successful
				token = res.getString(JSONKeys.TOKEN.toString());// update the token
				// #if Logging
				logger.info(this.getClass().getName(), String.format("User '%s' loggerged in.", username));
				// #endif
				return true;
			}
		} catch (JSONException e) {
			// #if Logging
			logger.error(this.getClass().getName(), String.format("JSONException occured. %s", ExceptionUtils.getStackTrace(e)));
			// #endif
		}

		// #if Logging
		logger.error(this.getClass().getName(), "Failed loggerin attempt.");
		// #endif
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

		// #if Logging
		logger.debug(this.getClass().getName(), "Messages are tried to be updated. Response from the server received.");
		// #endif
		JSONObject res = sendData(encrypt(message.toString(), Algorithms.AES));

		// if message sending was successful
		if (res.getString(JSONKeys.RESULT_CODE.toString()).equals(ResultCodes.OK.toString())) {
			// #if Logging
			logger.debug(this.getClass().getName(), "Messages have been updated");
			// #endif
			
			//refreshUI(res.getJSONArray(JSONKeys.MESSAGES.toString()));
			chatBackEnd.updateMessages(res.getJSONArray(JSONKeys.MESSAGES.toString()));// update all the messages
		}
		// try to reauthenticate when server returns NotAuthenticated ResultCode
		else if (res.getString(JSONKeys.RESULT_CODE.toString()).equals(ResultCodes.NotAuthenticated.toString())) {
			Authenticate(this.username, this.password);
		} else {
			// #if Logging
			logger.error(this.getClass().getName(), String.format("Something went wrong with messages update. Response code: %s.",
					res.getString(JSONKeys.RESULT_CODE.toString())));
			// #endif
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
	// #if !CLI
//@	public void sendMessage(String text, Color color, Algorithms encryptionAlg) {
//@		JSONObject message = new JSONObject();
//@		message.put(JSONKeys.ACTION_TYPE.toString(), ActionType.SEND_MESSAGE.toString());
//@		message.put(JSONKeys.TOKEN.toString(), token);
//@		message.put(JSONKeys.TEXT.toString(), text);
//@		message.put(JSONKeys.COLOR.toString(), color.toString());
//@		message.put(JSONKeys.USERNAME.toString(), username);
//@
	// #if Logging
//@		logger.debug(this.getClass().getName(), "Message is tried to be sent.");
	// #endif
//@		JSONObject res = sendData(encrypt(message.toString(), encryptionAlg));
//@
//@		// if message sending was successful
//@		if (res.getString(JSONKeys.RESULT_CODE.toString()).equals(ResultCodes.OK.toString())) {
//@			chatWindow.updateMessages(res.getJSONArray(JSONKeys.MESSAGES.toString()));// update all the messages
	// #if Logging
//@			logger.info(this.getClass().getName(), String.format("Message with text: '%s' send in color: '%s'.", text, color.toString()));
	// #endif
//@		} else {
	// #if Logging
//@			logger.error(this.getClass().getName(), String.format("Something went wrong with message sending. Response code: %s",
//@					res.getString(JSONKeys.RESULT_CODE.toString())));
	// #endif
//@		}
//@	}
	// #endif
	/**
	 * sends the data to the server and returns the result as a JSONObject.
	 * 
	 */

	private JSONObject sendData(String data) {
		JSONObject output;
		try {
			Socket skt = new Socket("localhost", portNumber);
			// send the data

			// #if Logging
			logger.debug(this.getClass().getName(), "Socket with localhost opened.");
			// #endif
			PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
			out.println(data);
			// #if Logging
			logger.debug(this.getClass().getName(), "Data sent to the server. Waiting for the response.");
			// #endif

			// receive the reply.
			BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
			while (!in.ready()) {
			}
			// #if Logging
			logger.debug(this.getClass().getName(), "Response from the server is ready.");
			// #endif

			output = new JSONObject(decrypt(in.readLine()));// Read one line, decrypt and output it
			// #if Logging
			logger.debug(this.getClass().getName(), "Response received.");
			// #endif
			out.close();
			in.close();
			skt.close();
			// #if Logging
			logger.debug(this.getClass().getName(), "Socket, buffer in and printer our are closed.");
			// #endif
		} catch (JSONException e) {
			output = new JSONObject();
			output.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.JSONParseError.toString());
			// #if Logging
			logger.error(this.getClass().getName(), String.format("JSONParseError occured when trying to send data. %s",
					ExceptionUtils.getStackTrace(e)));
			// #endif

		} catch (Exception e) {
			output = new JSONObject();
			output.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.Failed.toString());
			// #if Logging
			logger.error(this.getClass().getName(), String.format("An error occured when trying to send data. %s", ExceptionUtils.getStackTrace(e)));
			// #endif
		}

		// #if Logging
		logger.debug(this.getClass().getName(), "Data was successfully sent");
		// #endif
		return output;
	}

	// #if !CLI
//@	public ChatWindow getChatWindow() {
//@			return chatWindow;
//@	}
//@
	// #if Authentication
//@	public Authentication getAuthentication() {
//@		return authentication;
//@	}
	// #endif
	// #endif

	/**
	 * function that decrypts the input applying the decryption algorithm specified
	 * in json
	 * 
	 * @param s encrypted string
	 * @return decrypted string
	 */
	private String decrypt(String encryptedMessage) {
		JSONObject incomingJson = new JSONObject(encryptedMessage);
		String encryptionType = incomingJson.getString(JSONKeys.ENCRYPTION.toString());
		Encryption encryptionClass;

		if (encryptionType.equals(Algorithms.AES.toString())) {
			// #if Logging
			logger.debug(this.getClass().getName(), "Encryption: AES.");
			// #endif
			encryptionClass = new AESEncryption(jsonEncryptionKey);
		} else { // encryption is string reverse
			// #if Logging
			logger.debug(this.getClass().getName(), "Encryption: Reverse string.");
			// #endif
			encryptionClass = new ReverseStringEncryption();
		}
		String originalMessage = encryptionClass.decrypt(incomingJson.getString(JSONKeys.ENCRYPTED_MESSAGE.toString()));
		// #if Logging
		logger.debug(this.getClass().getName(), "Data was successfully decrypted.");
		// #endif
		return originalMessage;
	}

	private String encrypt(String message, Algorithms encryptionAlg) {
		JSONObject jsonForConnection = new JSONObject();
		jsonForConnection.put(JSONKeys.ENCRYPTION.toString(), encryptionAlg.toString());
		Encryption encryptionClass;

		if (encryptionAlg.equals(Algorithms.AES)) {
			// #if Logging
			logger.debug(this.getClass().getName(), "Encryption: AES.");
			// #endif
			encryptionClass = new AESEncryption(jsonEncryptionKey);
		} else { // encryption is string reverse
			// #if Logging
			logger.debug(this.getClass().getName(), "Encryption: Reverse string.");
			// #endif
			encryptionClass = new ReverseStringEncryption();
		}

		jsonForConnection.put(JSONKeys.ENCRYPTED_MESSAGE.toString(), encryptionClass.encrypt(message));
		// #if Logging
		logger.debug(this.getClass().getName(), "Data was successfully encrypted.");
		// #endif
		return jsonForConnection.toString();
	}

}
