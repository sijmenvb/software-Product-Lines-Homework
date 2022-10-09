package logging;

import org.apache.log4j.Logger;
import main.LoggingInterface;

public class Logging implements LoggingInterface {

	private Logger logger;

	
	public void Init() {
		logger = Logger.getLogger(Logging.class.getName());
	}

	
	public void debug(String className, String message) {
		logger.debug(className + " - " + message);
	}

	
	public void debug(String className, String message, Throwable throwable) {
		logger.debug(className + " - " + message, throwable);
	}


	public void info(String className, String message) {
		logger.info(className + " - " + message);

	}


	public void info(String className, String message, Throwable throwable) {
		logger.info(className + " - " + message, throwable);

	}


	public void warn(String className, String message) {
		logger.warn(className + " - " + message);

	}


	public void warn(String className, String message, Throwable throwable) {
		logger.warn(className + " - " + message, throwable);

	}


	public void error(String className, String message) {
		logger.error(className + " - " + message);

	}


	public void error(String className, String message, Throwable throwable) {
		logger.error(className + " - " + message, throwable);

	}

	
	public void fatal(String className, String message) {
		logger.fatal(className + " - " + message);

	}


	public void fatal(String className, String message, Throwable throwable) {
		logger.fatal(className + " - " + message, throwable);
	}
}