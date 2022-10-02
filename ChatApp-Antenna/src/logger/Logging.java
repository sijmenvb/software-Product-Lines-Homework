package logger;

import org.apache.log4j.Logger;
import main.LoggingInterface;

public class Logging implements LoggingInterface {

	private Logger logger;

	@Override
	public void Init() {
		logger = Logger.getLogger(Logging.class.getName());
	}

	@Override
	public void debug(String className, String message) {
		logger.debug(className + " - " + message);
	}

	@Override
	public void debug(String className, String message, Throwable throwable) {
		logger.debug(className + " - " + message, throwable);
	}

	@Override
	public void info(String className, String message) {
		logger.info(className + " - " + message);

	}

	@Override
	public void info(String className, String message, Throwable throwable) {
		logger.info(className + " - " + message, throwable);

	}

	@Override
	public void warn(String className, String message) {
		logger.warn(className + " - " + message);

	}

	@Override
	public void warn(String className, String message, Throwable throwable) {
		logger.warn(className + " - " + message, throwable);

	}

	@Override
	public void error(String className, String message) {
		logger.error(className + " - " + message);

	}

	@Override
	public void error(String className, String message, Throwable throwable) {
		logger.error(className + " - " + message, throwable);

	}

	@Override
	public void fatal(String className, String message) {
		logger.fatal(className + " - " + message);

	}

	@Override
	public void fatal(String className, String message, Throwable throwable) {
		logger.fatal(className + " - " + message, throwable);
	}
}