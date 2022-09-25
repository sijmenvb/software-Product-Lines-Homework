package enums;

public enum ActionType {
	AUTHENTICATION("authentication"), SEND_MESSAGE("sendMessage"), UPDATE_MESSAGES("updateMessages");

	// the code below make sit possible to change the name of the ENUM without
	// having to change the code.
	private final String label;

	ActionType(String s) {
		label = s;
	}

	@Override
	public String toString() {
		return label;
	}
	
	public static ActionType getEnum(String value) {
		for(ActionType a : values())
			if(a.toString().equals(value)) return a;
		throw new IllegalArgumentException();
	}
}
