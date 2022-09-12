package backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import DAL.Messages;
import DAL.Users;

import models.Message;
import models.User;

public class Communication {
	private int portNumber;
	static Logger log = Logger.getLogger(Communication.class.getName());

	// token source:
	// https://simplesolution.dev/java-json-web-token-using-java-jwt-library/
	SecretKey tokenKey;// key for generating tokens
	Algorithm algorithm;// algorithm used for token encryption.

	public Communication(int portNr) {
		this.portNumber = portNr;

		// generate a key
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(256);
			tokenKey = keyGen.generateKey();
			algorithm = Algorithm.HMAC512(tokenKey.toString());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * The main server communication point, where it gets requests and responds to
	 * the clients.
	 */
	public void communication() {
		try {
			while (true) {
				// Setup socket and create a BufferedReader for coming messages
				ServerSocket server = new ServerSocket(portNumber);
				Socket socket = server.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				log.debug(String.format("Server socket started with the port: %s.", portNumber));
				try {
					JSONObject json = readIncommingMessage(in);
					String actionType = json.getString(JSONKeys.ACTION_TYPE.toString());
					log.debug(String.format("Server got a message with an action type: %s.", actionType));
					switch (ActionType.getEnum(actionType)) {
					case AUTHENTICATION:
						if (login(json)) {
							sendToken(json.getString(JSONKeys.USERNAME.toString()), socket);
						} else {
							sendFailed(socket);
						}
						break;
					case SEND_MESSAGE:
						if (verifyToken(json.getString(JSONKeys.TOKEN.toString()),
								json.getString(JSONKeys.USERNAME.toString()))) {

							if (receiveMessage(json)) {
								sendAllMessages(socket);
							} else {
								System.out.println("Failed!");
								sendFailed(socket);
							}
						} else {
							sendNotAuthenticated(socket);// if token not valid
						}
						break;
					case UPDATE_MESSAGES:
						if (verifyToken(json.getString(JSONKeys.TOKEN.toString()),
								json.getString(JSONKeys.USERNAME.toString()))) {
							sendAllMessages(socket);
						} else {
							sendNotAuthenticated(socket);// if token not valid
						}

						break;
					default:
						sendFailed(socket);
						break;
					}
				} catch (JSONException e) {
					sendJSONParseError(socket);
				}
				log.debug("Closing all BufferedReader and sockets.");
				in.close();
				socket.close();
				server.close();
				log.debug("Buffered reader and sockets closed.");
			}
		} catch (Exception e) {
			log.error(String.format("Error occured while running the server. %s", ExceptionUtils.getStackTrace(e)));
		}
	}

	/**
	 * The function takes the ServerSocket parameter and reads the incoming message
	 * from it.
	 * 
	 * @param in is the buffer reader to get the incoming data from
	 * @return JSON object with the message data
	 */
	private JSONObject readIncommingMessage(BufferedReader in) {
		JSONObject output;
		try {
			while (!in.ready()) {
			} // Buffer reader not ready
			output = new JSONObject(in.readLine()); // Read one line and output it

			log.debug("Incoming message read and processed.");
			return output;

		} catch (JSONException e) {
			log.error(String.format("Error occured while reading incomming message. %s",
					ExceptionUtils.getStackTrace(e)));
			output = new JSONObject();
			output.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.JSONParseError.toString());
			return output;
		} catch (Exception e) {
			log.error(String.format("Error occured while reading incomming message. %s",
					ExceptionUtils.getStackTrace(e)));
			output = new JSONObject();
			output.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.Failed.toString());
			return output;
		}
	}

	/**
	 * Logs in user with the specified in the JSON object credentials.
	 * 
	 * @param json credentials of the user
	 * @return boolean value whether login succeeded
	 */
	private Boolean login(JSONObject json) {
		String username = json.getString(JSONKeys.USERNAME.toString());
		String password = json.getString(JSONKeys.PASSWORD.toString());
		User user = Users.selectByUsername(username);

		if (user.getPassword().equals(password)) {
			log.info(String.format("User '%s' logged in successfully.", username));
			return true;
		} else {
			log.info(String.format("User '%s' login did not succeed. Wrong password!", username));
			return false;
		}
	}

	/**
	 * Function takes a message data in JSON format and saves it to the local
	 * database.
	 * 
	 * @param json data with the message information
	 * @return boolean value whether message information saved successfully
	 */
	private Boolean receiveMessage(JSONObject json) {
		try {
			String text = json.getString(JSONKeys.TEXT.toString());
			String token = json.getString(JSONKeys.TOKEN.toString());
			String color = json.getString(JSONKeys.COLOR.toString());
			Boolean success = Messages.insert(text, token, color) > 0 ? true : false;
			if (success) {
				log.info(String.format("User '%s' sent a message '%s'.", token, text));
			} else {
				log.error(String.format("Message from user '%s' was not saved.", token));
			}
			return success;
		} catch (JSONException e) {
			log.error(String.format("Error occured while saving the message. %s", ExceptionUtils.getStackTrace(e)));
			return false;
		}

	}

	/**
	 * Retrieves all the messages from the database and sends them to the client.
	 * 
	 * @param server socket server to send the data to
	 */
	private void sendAllMessages(Socket socket) {
		JSONObject output = new JSONObject();
		try {
			log.debug("Starting retrieving the messages from the database.");
			JSONArray messageArray = new JSONArray();
			LinkedList<Message> dbMessages = Messages.selectAll();
			for (Iterator<Message> iterator = dbMessages.iterator(); iterator.hasNext();) {
				Message message = (Message) iterator.next();
				JSONObject messageJSON = new JSONObject();
				messageJSON.put(JSONKeys.TEXT.toString(), message.getText());
				messageJSON.put(JSONKeys.TOKEN.toString(), message.getToken());
				messageJSON.put(JSONKeys.COLOR.toString(), message.getColor());
				messageArray.put(messageJSON);
			}
			output.put(JSONKeys.MESSAGES.toString(), messageArray);
			output.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.OK.toString());
			log.debug("All the messages retrieved from the database.");
		} catch (JSONException e) {
			log.error(String.format("Error occured while retrieving all the messages. %s",
					ExceptionUtils.getStackTrace(e)));
			output = new JSONObject();
			output.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.JSONParseError.toString());
		}

		try {
			log.debug("Sending all the messages to the user.");
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.print(output.toString());

			out.close();
			log.debug("All the messages sent to the client.");

		} catch (Exception e) {
			log.error(String.format("Error occured while sending array of all the messages. %s",
					ExceptionUtils.getStackTrace(e)));
			System.out.println("Sending all messages failed.");
		}
	}

	/**
	 * check if the token is valid for this user.
	 * 
	 * @param token
	 * @param username
	 * @return
	 */
	private boolean verifyToken(String token, String username) {
		try {
			JWTVerifier verifier = JWT.require(algorithm).withIssuer("Simple Solution").build();

			DecodedJWT decodedJWT = verifier.verify(token);

			return decodedJWT.getClaims().get("username").equals(username);
		} catch (JWTVerificationException ex) {
			return false;
		}
	}

	/**
	 * Sends user token to the client.
	 * 
	 * @param token  to be send
	 * @param server socket server to send the data to
	 */

	private void sendToken(String username, Socket socket) {
		long expireTime = (new Date().getTime()) + 60000; // 60000 milliseconds = 60 seconds = 1 minute
		Date expireDate = new Date(expireTime);

		// create token
		System.out.println(tokenKey.toString());
		String token = JWT.create().withIssuer("Simple Solution").withClaim("username", username)
				.withClaim("role", "User").withExpiresAt(expireDate).sign(algorithm);
		System.out.println(token);

		// send token
		try {
			log.debug(String.format("Sending token '%s' to the client.", token));
			JSONObject message = new JSONObject();
			message.put(JSONKeys.TOKEN.toString(), token);
			message.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.OK.toString());

			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.print(message.toString());

			out.close();
			log.debug(String.format("Token '%s' sent to the client.", token));
		} catch (Exception e) {
			log.error(String.format("Sending token response failed. %s", ExceptionUtils.getStackTrace(e)));
		}
	}

	/**
	 * Sends success message to the client.
	 * 
	 * @param server socket server to send the data to
	 */
	private void sendSuccess(Socket socket) {
		try {
			log.debug("Sending OK response to the client.");
			JSONObject message = new JSONObject();
			message.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.OK.toString());

			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.print(message.toString());

			out.close();
			log.debug("OK response sent to the client.");
		} catch (Exception e) {
			log.error(String.format("Sending OK response failed. %s", ExceptionUtils.getStackTrace(e)));
		}
	}

	/**
	 * Sends JSONParseError message to the client.
	 * 
	 * @param server socket server to send the data to
	 */
	private void sendJSONParseError(Socket socket) {
		try {
			log.debug("Sending JSONParseError response to the client.");
			JSONObject message = new JSONObject();
			message.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.JSONParseError.toString());

			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.print(message.toString());
			out.close();
			log.debug("JSONParseError response sent to the client.");
		} catch (Exception e) {
			log.error(String.format("Sending JSONParseError response failed. %s", ExceptionUtils.getStackTrace(e)));
		}
	}

	/**
	 * Sends "failed" message to the client.
	 * 
	 * @param server socket server to send the data to
	 */
	private void sendFailed(Socket socket) {
		try {
			log.debug("Sending Failed response to the client.");
			JSONObject message = new JSONObject();
			message.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.Failed.toString());

			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.print(message.toString());
			out.close();
			log.debug("Failed response sent to the client.");
		} catch (Exception e) {
			log.error(String.format("Sending Failed response failed. %s", ExceptionUtils.getStackTrace(e)));
		}
	}

	/**
	 * Sends "notAuthenticated" message to the client.
	 * 
	 * @param server socket server to send the data to
	 */
	private void sendNotAuthenticated(Socket socket) {
		JSONObject message = new JSONObject();
		message.put(JSONKeys.RESULT_CODE.toString(), ResultCodes.NotAuthenticated.toString());
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.print(message.toString());
			out.close();
		} catch (Exception e) {
			System.out.println("Sending Failed response failed.");
		}

	}

}
