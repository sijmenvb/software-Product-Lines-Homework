
package authentication;

import javafx.scene.control.PasswordField;

//#if Logging
//@import org.apache.log4j.Logger;
//#endif

import backend.ServerConnection;
import gui.AuthenticationInterface;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

//login form source: https://www.javatpoint.com/login-form-java
public class GUIAuthentication extends VBox implements AuthenticationInterface {
	private ServerConnection serverConnectionRef;

	// initialize button, panel, label, and text field
	Button b1;
	Label userLabel, passLabel, feedbackLabel;
	private TextField textField1;
	private PasswordField textField2;

	private int loginAttempts = 0;
	private boolean isLoggedIn = false;
	private static boolean usesJavafx = true;

	private Stage primaryStage;
	private Scene nextScene;

	//#if Logging
//@	static Logger log = Logger.getLogger(GUIAuthentication.class.getName());
	//#endif

	// calling constructor
	public GUIAuthentication() {
		//#if Logging
//@		log.debug("Authentication window created.");
		//#endif
		// set the next page
	}
	
	@Override
	public void start() {
		// create label for username
				userLabel = new Label();
				userLabel.setText("Username"); // set label value for textField1

				// create text field to get username from the user
				textField1 = new TextField();

				// create label for password
				passLabel = new Label();
				passLabel.setText("Password"); // set label value for textField2

				// create text field to get password from the user
				textField2 = new PasswordField();

				// create submit button
				b1 = new Button("SUBMIT"); // set label to button

				// create label for feedback to the user
				feedbackLabel = new Label();

				// create panel to put form elements
				this.getChildren().addAll(userLabel, textField1, passLabel, textField2, b1, feedbackLabel);

				// perform action on button click
				b1.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent arg0) {
						handleCredentials(textField1.getText(), textField2.getText());
					}
				});	
	}

	// verify the credentials entered by the user
	public void handleCredentials(String inputUsername, String inputPassword) {
		if (verifyCredential(inputUsername, inputPassword)) {
			primaryStage.setScene(nextScene);
			// populate the chatBox
			serverConnectionRef.updateMessages();
			isLoggedIn = true;
			//#if Logging
//@			log.debug("Credentials of the user successfully verified.");
			//#endif
			return;
		}
		feedbackLabel.setText(String.format("(%d) username/password invalid", loginAttempts));
		loginAttempts++;
		//#if Logging
//@		log.debug("Failed credentials verification attempt.");
		//#endif

	}

	@Override
	public boolean verifyCredential(String username, String password) {
		return serverConnectionRef.firstAuthentication(username, password);
	}

	@Override
	public boolean userIsLoggedIn() {
		return isLoggedIn;
	}
	
	@Override
	public boolean usesJavafx() {
		return usesJavafx;
	}

	@Override
	public void setServerConnection(ServerConnection serverConnection) {
		serverConnectionRef = serverConnection;
	}

	@Override
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;		
	}

	@Override
	public void setNextScene(Scene nextScene) {
		this.nextScene = nextScene;		
	}
	


}
