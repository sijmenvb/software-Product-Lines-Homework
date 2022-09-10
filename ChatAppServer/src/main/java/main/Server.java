package main;

import java.io.*;
import java.net.*;

// Server code taken from https://www.ashishmyles.com/tutorials/tcpchat/index.html 
public class Server {
	static int portNumber = 42069;
	
	public static void main(String args[]) {
		try {
			ServerSocket srvr;
			while(true) {
				srvr = new ServerSocket(portNumber);
				System.out.println(String.format("Server socket started with the port: %s.", portNumber));
	            Socket skt = srvr.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
				while (!in.ready()) {} // Buffer reader not ready
				System.out.println(in.readLine()); // Read one line and output it
				in.close();
		        skt.close();
		        srvr.close();
			}
		}
	    catch(Exception e) {
	    	e.printStackTrace();
	    }
	}
}