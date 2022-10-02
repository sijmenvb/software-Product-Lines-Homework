package logger.interfaces;

public interface ILogging {
	void debug(String className, String message);
	void debug(String className, String message, Throwable throwable);
	void info(String className, String message);
	void info(String className, String message, Throwable throwable);
	void warn(String className, String message);
	void warn(String className, String message, Throwable throwable);
	void error(String className, String message);
	void error(String className, String message, Throwable throwable);
	void fatal(String className, String message);
	void fatal(String className, String message, Throwable throwable);
}
