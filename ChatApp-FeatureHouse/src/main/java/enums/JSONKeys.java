package enums; 

public enum  JSONKeys {
	ACTION_TYPE("actionType") ,  TOKEN("token") ,  TEXT("text") ,  COLOR("color") ,  USERNAME("username") ,  PASSWORD("password") , 
	RESULT_CODE("resultCode") ,  MESSAGES("messages") ,  ENCRYPTION("encryptionAlgorithm") ,  ENCRYPTED_MESSAGE("encryptedMessage"); 

	// the code below make sit possible to change the name of the ENUM without
	// having to change the code.
	private final String label; 

	JSONKeys(String s) {
		label = s;
	} 

	@Override
	public String toString() {
		return label;
	}}
