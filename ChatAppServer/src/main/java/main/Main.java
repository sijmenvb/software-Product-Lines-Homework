package main;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import backend.Communication;
import DAL.Users;
import DAL.AES;
import models.User; 

// Server code taken from https://www.ashishmyles.com/tutorials/tcpchat/index.html 
public class Main {
	static int portNumber = 42069;
	static Logger log = Logger.getLogger(Main.class.getName()); 
	static String user = "admin";
	static String pass = "admin";

	/**
	 * Main function of the server. 
	 * Runs the SocketServer at the specified port and every loop iteration accepts the socket message 
	 *   and takes actions specified by the message.
	 *   
	 * @param args
	 */
	
	public static void main(String args[]) {
		try {
			while (true) {
				System.out.println("Server is running");
				ServerSocket srvr = new ServerSocket(portNumber);
				log.debug(String.format("Server socket started with the port: %s.", portNumber));
				/*
				 * int id = Users.insert("elanto", "VeryStrongPassword");
				 * System.out.println("New id: " + id); LinkedList<User> users =
				 * Users.selectAll(); User fst = users.get(0);
				 * System.out.println(String.format("Id: %d, username: %s, password: %s.",
				 * fst.getId(), fst.getUsername(), fst.getPassword()));
				 */
				Socket skt = srvr.accept();
				log.debug("Connection for the input opened.");
				BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
				log.debug("Message from the connection received.");
				while (!in.ready()) {
				} // Buffer reader not ready
				System.out.println("This is the server speaking");
				System.out.println(decrypt(in.readLine()));
				in.close();
				log.debug("Buffer closed.");
				skt.close();
				log.debug("Connection closed.");
				srvr.close();
				log.debug("Server socket stopped.");
			}
			Users.insert(user, pass);
		} catch (Exception e) {
			log.error(String.format("Error occured while adding user. %s", ExceptionUtils.getStackTrace(e)));
		}
		Communication comm = new Communication(portNumber);
		comm.communication();
	}

	// placeholder decryption function
	private static String decrypt(String s) {
		System.out.println(s);
		StringBuilder s_reverse = new StringBuilder(AES.decrypt(s, "key"));
		s = s_reverse.reverse().toString();
		System.out.println(s);
		return s;
	}
	
	
	
}
