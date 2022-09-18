package backend;

public  class Configuration {
	//all arguments are case insensitive 
	
	//color selection true by default disabled by -disableColor
	public boolean COLOR = true;
	//logging default is logging can be set by debug using -debug
	public boolean DEBUG = false;
	//disable logging by using -nolog
	public boolean LOGGING = true;
	
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
			default:
				System.err.println(args[i]+ " is not a recognised argument!");
				break;
			}
		}
	}
}
