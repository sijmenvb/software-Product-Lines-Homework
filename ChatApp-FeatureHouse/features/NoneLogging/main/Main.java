package main;

import main.LoggingInterface;
import logging.NullLogging;

public class Main {
	private static LoggingInterface getlogger() {
		return new NullLogging();
	}
}
