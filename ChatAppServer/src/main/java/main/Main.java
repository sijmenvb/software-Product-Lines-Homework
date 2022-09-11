package main;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import DAL.Messages;
import DAL.Users;
import backend.ActionType;
import backend.JSONKeys;
import backend.ResultCodes;
import models.Message;
import models.User;

// Server code taken from https://www.ashishmyles.com/tutorials/tcpchat/index.html 
public class Main {
	static int portNumber = 42069;
	
	/**
	 * Main function of the server. 
	 * Runs the SocketServer at the specified port and every loop iteration accepts the socket message 
	 *   and takes actions specified by the message.
	 *   
	 * @param args
	 */
	
	public static void main(String args[]) {
		try {
			LinkedList<Message> dbMessages = Messages.selectAll();
			System.out.println(dbMessages.size());
			while(true) {
				ServerSocket server = new ServerSocket(portNumber);
				System.out.println(String.format("Server socket started with the port: %s.", portNumber));
				try {
					JSONObject json = readIncommingMessage(server);
					String actionType = json.getString(JSONKeys.ACTION_TYPE.toString());
					switch (ActionType.getEnum(actionType)) {
						case AUTHENTICATION:
							if (login(json)) {
								sendToken(json.getString(JSONKeys.USERNAME.toString()), server);
							} else {
								sendFailed(server);
							}
							break;
						case SEND_MESSAGE:
							System.out.println("Send message!");
							if (receiveMessage(json)) {
								sendAllMessages(server);
							} else {
								System.out.println("Failed!");
								sendFailed(server);
							}
							break;
						case UPDATE_MESSAGES:
							sendAllMessages(server);
							break;
						default:
							sendFailed(server);
							break;
					}
				} catch (JSONException e) {
					sendJSONParseError(server);
				}
				
				server.close();
			}
		}
	    catch(Exception e) {
	    	e.printStackTrace();
	    }
	}

	/**
	 * The function takes the ServerSocket parameter and reads the incoming message from it.
	 * 
	 * @param server is the server socket where the information is read from.
	 * @return JSON object with the message data
	 */
	private static JSONObject readIncommingMessage(ServerSocket server) {
		JSONObject output;
		try {
			Socket skt = server.accept();
			BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
			while (!in.ready()) {} // Buffer reader not ready
			output = new JSONObject(in.readLine()); // Read one line and output it
			in.close();
	        skt.close();
	        return output;
		} catch (JSONException e) {
			output = new JSONObject();
			output.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.JSONParseError.toString());
	        return output;
		} catch (Exception e) {
			output = new JSONObject();
			output.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.Failed.toString());
	        return output;
		}		
	}
	
	/**
	 * Logs in user with the specified in the JSON object credentials.
	 * @param json credentials of the user
	 * @return boolean value whether login succeeded
	 */
	private static Boolean login(JSONObject json) {
		String username = json.getString(JSONKeys.USERNAME.toString());
		String password = json.getString(JSONKeys.PASSWORD.toString());
		User user = Users.selectByUsername(username);
		if(user.getPassword() == password) {
			System.out.println("User login succeeded!");
			return true;
		}else {
			System.out.println("Wrong password, user login did not succeed!");
			return false;
		}
	}
	
	/**
	 * Function takes a message data in JSON format and saves it to the local database. 
	 * @param json data with the message information
	 * @return boolean value whether message information saved successfully
	 */
	private static Boolean receiveMessage(JSONObject json) {
		System.out.println("Update messages!");
		String text = json.getString(JSONKeys.TEXT.toString());
		String token = json.getString(JSONKeys.TOKEN.toString());
		String color = json.getString(JSONKeys.COLOR.toString());
		int id = Messages.insert(text, token, color);
		return id > 0 ? true : false;
	}

	/**
	 * Retrieves all the messages from the database and sends them to the client.
	 * @param server socket server to send the data to
	 */
	private static void sendAllMessages(ServerSocket server) {
		JSONObject output = new JSONObject();
		try {
			JSONArray messageArray = new JSONArray();
			LinkedList<Message> dbMessages = Messages.selectAll();
			for (Iterator<Message> iterator = dbMessages.iterator(); iterator.hasNext();) {
				Message message = (Message) iterator.next();
				JSONObject messageJSON = new JSONObject();
				messageJSON.put(JSONKeys.TEXT.toString(), message.getText());
				messageJSON.put(JSONKeys.TOKEN.toString(), message.getSender());
				messageJSON.put(JSONKeys.COLOR.toString(), message.getColor());
				messageArray.put(messageJSON.toString());
			}
			output.put(JSONKeys.MESSAGES.toString(), messageArray.toString());
			output.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.OK.toString());
		} catch (JSONException e) {
			output = new JSONObject();
			output.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.JSONParseError.toString());
		}
		
		try {
			Socket skt = server.accept();
			PrintWriter out = new PrintWriter(skt.getOutputStream());
			out.print(output.toString());
		 	out.close();
		} catch (Exception e) {
			System.out.println("Sending OK response failed.");
		}		
	}
	
	/**
	 * Sends user token to the client.
	 * @param token to be send
	 * @param server socket server to send the data to
	 */
	private static void sendToken(String token, ServerSocket server) {
		JSONObject message = new JSONObject();
		message.put(JSONKeys.TOKEN.toString(), token);
		message.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.OK.toString());
		try {
			Socket skt = server.accept();
			PrintWriter out = new PrintWriter(skt.getOutputStream());
			out.print(message.toString());
		 	out.close();
		} catch (Exception e) {
			System.out.println("Sending OK response failed.");
		}	
	}
	
	/**
	 * Sends success message to the client.
	 * @param server socket server to send the data to
	 */
	private static void sendSuccess(ServerSocket server) {
		JSONObject message = new JSONObject();
		message.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.OK.toString());
		try {
			Socket skt = server.accept();
			PrintWriter out = new PrintWriter(skt.getOutputStream());
			out.print(message.toString());
		 	out.close();
		} catch (Exception e) {
			System.out.println("Sending OK response failed.");
		}	
	}
	
	/**
	 * Sends JSONParseError message to the client.
	 * @param server socket server to send the data to
	 */
	private static void sendJSONParseError(ServerSocket server) {
		JSONObject message = new JSONObject();
		message.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.JSONParseError.toString());
		try {
			Socket skt = server.accept();
			PrintWriter out = new PrintWriter(skt.getOutputStream());
			out.print(message.toString());
		 	out.close();
		} catch (Exception e) {
			System.out.println("Sending OK response failed.");
		}	
	}
	
	/**
	 * Sends "failed" message to the client.
	 * @param server socket server to send the data to
	 */
	private static void sendFailed(ServerSocket server) {
		JSONObject message = new JSONObject();
		message.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.Failed.toString());
		try {
			Socket skt = server.accept();
			PrintWriter out = new PrintWriter(skt.getOutputStream());
			out.print(message.toString());
		 	out.close();
		} catch (Exception e) {
			System.out.println("Sending OK response failed.");
		}	
	}
}
