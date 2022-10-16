package backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.JSONException;
import org.json.JSONObject;

import enums.JSONKeys;
import enums.ActionType;
import enums.Algorithms;
import enums.ResultCodes;
import main.UIInterface;


public class ServerConnection {
	
	static final int portNumber = 42069;
	private UIInterface ui;
	private ChatBackEnd chatBackEnd;
	private String token = "";

	private String username = "";
	private String password = "";
	
	
	public ServerConnection(UIInterface ui) {
		this.ui = ui;
		this.chatBackEnd = new ChatBackEnd(this);
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


		JSONObject res = sendData(encrypt(message.toString()));

		try {
			if (res.getString(JSONKeys.RESULT_CODE.toString()).equals(ResultCodes.OK.toString())) {// if authentication
																									// was successful
				token = res.getString(JSONKeys.TOKEN.toString());// update the token
				return true;
			}
		} catch (JSONException e) {
			throw e;
		}
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


		JSONObject res = sendData(encrypt(message.toString()));


		// if message sending was successful
		if (res.getString(JSONKeys.RESULT_CODE.toString()).equals(ResultCodes.OK.toString())) {
			chatBackEnd.updateMessages(res.getJSONArray(JSONKeys.MESSAGES.toString()));// update all the messages
		}
		// try to reauthenticate when server returns NotAuthenticated ResultCode
		else if (res.getString(JSONKeys.RESULT_CODE.toString()).equals(ResultCodes.NotAuthenticated.toString())) {
			Authenticate(this.username, this.password);
		} else {
			//TODO!
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

			PrintWriter out = new PrintWriter(skt.getOutputStream(), true);

			out.println(data);
			// receive the reply.
			BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
			while (!in.ready()) {
			}

			output = new JSONObject(in.readLine());// Read one line, decrypt and output it
			out.close();
			in.close();
			skt.close();
		} catch (JSONException e) {
			output = new JSONObject();
			output.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.JSONParseError.toString());

		} catch (Exception e) {
			output = new JSONObject();
			output.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.Failed.toString());
		}

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
		String originalMessage = incomingJson.getString(JSONKeys.ENCRYPTED_MESSAGE.toString());
		return originalMessage;
	}

	private String encrypt(String message) {
		JSONObject jsonForConnection = new JSONObject();
		jsonForConnection.put(JSONKeys.ENCRYPTION.toString(), Algorithms.None.toString());
		jsonForConnection.put(JSONKeys.ENCRYPTED_MESSAGE.toString(), message);
		return jsonForConnection.toString();
	}

}
