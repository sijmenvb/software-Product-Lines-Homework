package backend;

public  class Configuration {
	//all arguments are case insensitive 
	
	//color selection true by default disabled by -disableColor
	public static boolean COLOR = true;
	//logging default is logging can be set by debug using -debug
	public static boolean DEBUG = false;
	//disable logging by using -nolog
	public static boolean LOGGING = true;
	
	public Configuration(String[] args) {
		for (int i = 0; i < args.length; i++) {
			switch (args[i].toLowerCase()) {
			case "-disablecolor":
				COLOR = false;
				break;
			case "-debug":
				DEBUG = true;
				break;
			case "-nolog":
				LOGGING = false;
				break;
			}
		}
	}
}
