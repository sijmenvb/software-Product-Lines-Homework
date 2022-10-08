package logging; 

import main.LoggingInterface; 

public  class  NullLogging  implements LoggingInterface {
	

	@Override
	public void Init() {
		return;
	}

	

	@Override
	public void debug(String className, String message) {
		return;
	}

	

	@Override
	public void debug(String className, String message, Throwable throwable) {
		return;
	}

	

	@Override
	public void info(String className, String message) {
		return;
	}

	

	@Override
	public void info(String className, String message, Throwable throwable) {
		return;
	}

	

	@Override
	public void warn(String className, String message) {
		return;
	}

	

	@Override
	public void warn(String className, String message, Throwable throwable) {
		return;
	}

	

	@Override
	public void error(String className, String message) {
		return;
	}

	

	@Override
	public void error(String className, String message, Throwable throwable) {
		return;
	}

	

	@Override
	public void fatal(String className, String message) {
		return;
	}

	

	@Override
	public void fatal(String className, String message, Throwable throwable) {
		return;
	}


}
