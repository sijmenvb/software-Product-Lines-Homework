package enums;

public enum Algorithms {
	AES("AES"), REVERSE("Reverse string"), None("No encryption");

	// the code below make sit possible to change the name of the ENUM without
	// having to change the code.
	private final String label;

	Algorithms(String s) {
		label = s;
	}

	@Override
	public String toString() {
		return label;
	}
	
	public static Algorithms fromString(String text) {
        for (Algorithms alg : Algorithms.values()) {
            if (alg.toString().equalsIgnoreCase(text)) {
                return alg;
            }
        }
        return null;
    }
}
