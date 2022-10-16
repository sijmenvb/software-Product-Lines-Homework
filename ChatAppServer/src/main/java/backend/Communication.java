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

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import enums.JSONKeys;
import enums.ActionType;
import enums.Algorithms;
import enums.ResultCodes;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import DAL.Messages;
import DAL.Users;
import encryption.AESEncryption;
import encryption.NoneEncryption;
import encryption.ReverseStringEncryption;
import encryption.Interfaces.Encryption;
import models.Message;
import models.User;

public class Communication {
	private int portNumber;
	private final String jsonEncryptionKey = "p:=l,]kHGv'eByu";
	static Logger log = Logger.getLogger(Communication.class.getName());
	private Algorithms lastUsedEncryption = Algorithms.AES;

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
							sendResultCode(socket, ResultCodes.Failed);
						}
						break;
					case SEND_MESSAGE:
						if (verifyToken(json.getString(JSONKeys.TOKEN.toString()),
								json.getString(JSONKeys.USERNAME.toString()))) {

							if (receiveMessage(json)) {
								sendAllMessages(socket);
							} else {
								System.out.println("Failed!");
								sendResultCode(socket, ResultCodes.Failed);
							}
						} else {
							sendResultCode(socket, ResultCodes.NotAuthenticated); // if token not valid
						}
						break;
					case UPDATE_MESSAGES:
						if (verifyToken(json.getString(JSONKeys.TOKEN.toString()),
								json.getString(JSONKeys.USERNAME.toString()))) {
							sendAllMessages(socket);
						} else {
							sendResultCode(socket, ResultCodes.NotAuthenticated); // if token not valid
						}

						break;
					default:
						sendResultCode(socket, ResultCodes.Failed);
						break;
					}
				} catch (JSONException e) {
					sendResultCode(socket, ResultCodes.JSONParseError);
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
			String messageIn = in.readLine();
			output = new JSONObject(decrypt(messageIn)); // Read one line and output it
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
	 * 
	 */
	private boolean verifyToken(String token, String username) {
		try {
			JWTVerifier verifier = JWT.require(algorithm).withIssuer("Simple Solution").build();

			DecodedJWT decodedJWT = verifier.verify(token);
			// TODO: figure oput how to get this as a propper string and not one with extra
			// quotes.
			return decodedJWT.getClaims().get(JSONKeys.USERNAME.toString()).toString().equals("\"" + username + "\"");
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
		String token = JWT.create().withIssuer("Simple Solution").withClaim(JSONKeys.USERNAME.toString(), username)
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
	 * Sends result code message to the client.
	 * 
	 * @param server socket server to send the data to
	 * @param result code
	 */
	private void sendResultCode(Socket socket, ResultCodes code) {
		try {
			log.debug("Sending JSONParseError response to the client.");
			JSONObject message = new JSONObject();
			message.put(JSONKeys.RESULT_CODE.toString(), code.toString());

			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.print(message.toString());
			out.close();
			log.debug(String.format("%s response sent to the client.", code.toString()));
		} catch (Exception e) {
			log.error(
					String.format("Sending %s response failed. %s", code.toString(), ExceptionUtils.getStackTrace(e)));
		}
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
		Encryption encryptionClass;

		if (encryptionType.equals(Algorithms.AES.toString())) {
			lastUsedEncryption = Algorithms.AES;
			log.debug("Encryption: AES.");
			encryptionClass = new AESEncryption(jsonEncryptionKey);
		} else if (encryptionType.equals(Algorithms.REVERSE.toString())) {  // encryption is string reverse
			lastUsedEncryption = Algorithms.REVERSE;
			log.debug("Encryption: Reverse string.");
			encryptionClass = new ReverseStringEncryption();
		} else { //if encryption is none
			lastUsedEncryption = Algorithms.REVERSE;
			log.debug("Encryption: None.");
			encryptionClass = new NoneEncryption();
		}

		System.out.println(incomingJson.getString(JSONKeys.ENCRYPTED_MESSAGE.toString()));
		String originalMessage = encryptionClass.decrypt(incomingJson.getString(JSONKeys.ENCRYPTED_MESSAGE.toString()));
		return originalMessage;
	}

	/**
	 * function that encrypts the input applying the encryption algorithm specified
	 * by parameter
	 * 
	 * @param message string to be encrypted
	 * @return encrypted string
	 */
	private String encrypt(String message) {
		JSONObject jsonForConnection = new JSONObject();
		jsonForConnection.put(JSONKeys.ENCRYPTION.toString(), lastUsedEncryption.toString());
		Encryption encryptionClass;

		if (lastUsedEncryption.equals(Algorithms.AES)) {
			log.debug("Encryption: AES.");
			encryptionClass = new AESEncryption(jsonEncryptionKey);
		} else if (lastUsedEncryption.equals(Algorithms.REVERSE)) { // encryption is string reverse
			log.debug("Encryption: Reverse string.");
			encryptionClass = new ReverseStringEncryption();
		}else {//if none encryption
			log.debug("Encryption: None.");
			encryptionClass = new NoneEncryption();
		}

		jsonForConnection.put(JSONKeys.ENCRYPTED_MESSAGE.toString(), encryptionClass.encrypt(message));
		return jsonForConnection.toString();
	}
}
