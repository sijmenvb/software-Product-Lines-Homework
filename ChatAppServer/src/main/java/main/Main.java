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
	
	public static void main(String args[]) {
		try {
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
							sendAllMessages(json, server);
							break;
						case UPDATE_MESSAGES:
							if (receiveMessage(json)) {
								sendAllMessages(json, server);
							} else {
								sendFailed(server);
							}
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
	
	private static Boolean receiveMessage(JSONObject json) {
		String text = json.getString(JSONKeys.TEXT.toString());
		String token = json.getString(JSONKeys.TOKEN.toString());
		String color = json.getString(JSONKeys.COLOR.toString());
		int id = Messages.insert(text, token, color);
		return id > 0 ? true : false;
	}

	private static void sendAllMessages(JSONObject json, ServerSocket server) {
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
