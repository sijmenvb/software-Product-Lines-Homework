package main;

import gui.AuthenticationInterface;
import logging.NullLogging;

public class Main {
	private LoggingInterface getlogger() {
		return new NullLogging();
	}
}
