package main;

import java.io.File;
import java.util.LinkedList;

import backend.ServerConnection;
import cli.CLI;

public class Main {
	public static void main(String[] args) {
		UIInterface ui = new CLI();
		AuthenticationInterface auth = getAuthenticator();
		LoggingInterface logger = getlogger();
		
		ui.start(auth, logger);
	}
}
