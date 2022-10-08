package main;

import gui.AuthenticationInterface;
import logging.NullLogging;

public class Main extends Application {
	private LoggingInterface getlogger() {
		return new NullLogging();
	}
}
