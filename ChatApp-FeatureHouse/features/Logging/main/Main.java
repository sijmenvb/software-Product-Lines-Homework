package main;

import main.AuthenticationInterface;
import logging.Logging;

public class Main {
	private static LoggingInterface getlogger() {
		Logging logger = new Logging();
		logger.Init();
		return logger;
	}
}

