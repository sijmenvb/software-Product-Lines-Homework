package backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import gui.ChatWindow;
import javafx.scene.paint.Color;

public class ServerConnection {
	static final int portNumber = 42069;
	private ChatWindow chatWindow;
	private String token = "";

	public ServerConnection() {
		this.chatWindow = new ChatWindow(this);
	}

	/**
	 * sends { "actionType" : "authentication", "username" : username, "password" :
	 * password}
	 * 
	 * expects { "resultCode" : "ok", "token" : new token}
	 * 
	 * will save the new token for further communication.
	 */
	public void Authenticate(String username, String password) {
		JSONObject message = new JSONObject();
		message.put(JSONKeys.ACTION_TYPE.toString(), ActionType.AUTHENTICATION.toString());
		message.put(JSONKeys.USERNAME.toString(), username);
		message.put(JSONKeys.PASSWORD.toString(), hash(password));

		JSONObject res = sendData(encrypt(message.toString()));

		// if authentication was successful
		if (res.getString(JSONKeys.RESULT_CODE.toString()) == ResultCodes.OK.toString()) {
			token = res.getString(JSONKeys.TOKEN.toString());// update the token
		}
	}

	// TODO: actually add password hashing
	private String hash(String s) {
		return s;
	}

	// TODO: actually add encryption
	private String encrypt(String s) {
		return s;
	}

	/**
	 * sends { "actionType" : "updateMessages", "token" : token}
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

		JSONObject res = sendData(encrypt(message.toString()));

		// if message sending was successful
		if (res.getString(JSONKeys.RESULT_CODE.toString()) == ResultCodes.OK.toString()) {
			chatWindow.updateMessages(res.getJSONArray(JSONKeys.MESSAGES.toString()));// update all the messages
		}
	}

	/**
	 * sends { "actionType" : "sendMessage", "token" : token, "text" : text, "color"
	 * : color}
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

		JSONObject res = sendData(encrypt(message.toString()));

		// if message sending was successful
		if (res.getString(JSONKeys.RESULT_CODE.toString()) == ResultCodes.OK.toString()) {
			chatWindow.updateMessages(res.getJSONArray(JSONKeys.MESSAGES.toString()));// update all the messages
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
			//send the data
			PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
			out.print(data);
			out.close();
			
			//receive the reply.
			BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
			while (!in.ready()) {
			}
			output = new JSONObject(in.readLine()); // Read one line and output it
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

	public ChatWindow getChatWindow() {
		return chatWindow;
	}
}