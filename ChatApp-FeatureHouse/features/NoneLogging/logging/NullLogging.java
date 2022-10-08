package logging;

import main.LoggingInterface;

public class NullLogging implements LoggingInterface {

	public void Init() {
		return;
	}

	public void debug(String className, String message) {
		return;
	}

	public void debug(String className, String message, Throwable throwable) {
		return;
	}

	public void info(String className, String message) {
		return;
	}

	public void info(String className, String message, Throwable throwable) {
		return;
	}

	public void warn(String className, String message) {
		return;
	}

	public void warn(String className, String message, Throwable throwable) {
		return;
	}

	public void error(String className, String message) {
		return;
	}

	public void error(String className, String message, Throwable throwable) {
		return;
	}

	public void fatal(String className, String message) {
		return;
	}

	public void fatal(String className, String message, Throwable throwable) {
		return;
	}

}
