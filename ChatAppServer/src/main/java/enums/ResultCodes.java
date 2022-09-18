package enums;

public enum ResultCodes {
	Failed("failed"), JSONParseError("jsonParseError"), NotAuthenticated("notAuthenticated"), OK("ok");

	// the code below make sit possible to change the name of the ENUM without
	// having to change the code.
	private final String label;

	ResultCodes(String s) {
		label = s;
	}

	@Override
	public String toString() {
		return label;
	}
}
