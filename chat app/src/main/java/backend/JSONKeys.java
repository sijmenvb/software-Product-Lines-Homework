package backend;

public enum JSONKeys {
	MessageType("messageType"), Token("token"), Text("text"), Color("color"), Username("username"),
	Password("password");

	// the code below make sit possible to change the name of the enum without
	// having to change the code.
	private final String label;

	JSONKeys(String s) {
		label = s;
	}

	@Override
	public String toString() {
		return label;
	}
}
