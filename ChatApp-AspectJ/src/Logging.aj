import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import backend.ServerConnection;
import backend.ChatBackEnd;

public aspect Logging {
//	after(): call(void log.debug()) {
//		System.out.print(" Logging SVEN");
//	}

	Logger logger = Logger.getLogger("ServerConnection");

	pointcut loginAttempt(String username, String hashedPassword) : 
		execution(boolean Authenticate(String, String)) && args(username, hashedPassword);

	boolean around(String username, String hashedPassword): loginAttempt(username, hashedPassword) {
		logger.debug(String.format("User '%s' is trying to log in. Response from the server received.", username));
		try {
			if (proceed(username, hashedPassword)) {
				logger.info(String.format("User '%s' logged in.", username));
				return true;
			}
		} catch (JSONException e) {
			logger.error(String.format("JSONException occured. %s", ExceptionUtils.getStackTrace(e)));
		}
		logger.error("Failed login attempt.");
		return false;

	}

	pointcut updateMessagesAttempt() : 
		execution(void updateMessages());

	void around(): updateMessagesAttempt() {
		logger.debug("Messages are tried to be updated. Response from the server received.");
		proceed();
		logger.error("Updating messages done.");

	}

	pointcut updateMessagesDone() : 
		execution(void ChatBackEnd.updateMessages(..)) && this(ServerConnection);

	after() : updateMessagesAttempt() {
		logger.debug("Messages have been updated SVEN");
	}

	pointcut updateMessagesReauthenticate() : 
		execution(boolean Authenticate(..)) && this(ServerConnection);

	after() : updateMessagesAttempt() {
		logger.debug("Reauthenticated");
	}

	pointcut sendDataAttempt(String data) : 
		execution(JSONObject sendData(String)) && args(data);

	JSONObject around(String data): sendDataAttempt(data) {
		try {
			JSONObject output = proceed(data);
			logger.debug("Data was successfully sent");
			return output;
		} catch (Exception e) {
			logger.error(String.format("JSONException occured. %s", ExceptionUtils.getStackTrace(e)));
		}
		return null;

	}
}
