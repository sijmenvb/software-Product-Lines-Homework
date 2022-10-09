package main;

import gui.AuthenticationInterface;
import logging.Logging;

public class Main {
	private LoggingInterface getlogger() {
		Logging logger = new Logging();
		logger.Init();
		return logger;
	}
}

