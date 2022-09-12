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
			Users.insert(user, pass);
		} catch (Exception e) {
			log.error(String.format("Error occured while adding user. %s", ExceptionUtils.getStackTrace(e)));
		}
		Communication comm = new Communication(portNumber);
		comm.communication();
	}
		
}
