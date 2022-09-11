package main;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

import DAL.Users;
import models.User;

// Server code taken from https://www.ashishmyles.com/tutorials/tcpchat/index.html 
public class Main {
	static int portNumber = 42069;
	
	public static void main(String args[]) {
		try {
			ServerSocket srvr = new ServerSocket(portNumber);
			System.out.println(String.format("Server socket started with the port: %s.", portNumber));
			while(true) {
				/* Playground 
	            int id = Users.insert("elanto", "VeryStrongPassword");
				System.out.println("New id: " + id);
				LinkedList<User> users = Users.selectAll();
				User fst = users.get(0);
				System.out.println(String.format("Id: %d, username: %s, password: %s.", fst.getId(), fst.getUsername(), fst.getPassword()));
				*/
				Socket skt = srvr.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
				while (!in.ready()) {} // Buffer reader not ready
				System.out.println(in.readLine()); // Read one line and output it
				in.close();
		        skt.close();
			}
		}
	    catch(Exception e) {
	    	e.printStackTrace();
	    }
	}
}
