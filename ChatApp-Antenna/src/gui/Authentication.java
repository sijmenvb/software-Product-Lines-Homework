//#if !CLI
package gui;

import javafx.scene.control.PasswordField;

//#if Logging
//@import org.apache.log4j.Logger;
//#endif

import backend.ServerConnection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

//login form source: https://www.javatpoint.com/login-form-java
public class Authentication extends VBox {
	private ServerConnection serverConnectionRef;

	// initialize button, panel, label, and text field
	Button b1;
	Label userLabel, passLabel, feedbackLabel;
	final TextField textField1;
	final PasswordField textField2;

	private int loginAttempts = 0;

	private Stage primaryStage;
	private Scene nextScene;

	//#if Logging
//@	static Logger log = Logger.getLogger(Authentication.class.getName());
	//#endif

	// calling constructor
	public Authentication(Stage primaryStage, Scene nextScene, ServerConnection serverConnection) {
		//#if Logging
//@		log.debug("Authentication window created.");
		//#endif
		serverConnectionRef = serverConnection;
		// set the next page
		this.primaryStage = primaryStage;
		this.nextScene = nextScene;

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
				verifyCredentials(textField1.getText(), textField2.getText());
			}
		});
	}

	// verify the credentials entered by the user
	public void verifyCredentials(String inputUsername, String inputPassword) {
		if (serverConnectionRef.firstAuthentication(inputUsername, inputPassword)) {
			primaryStage.setScene(nextScene);
			// populate the chatBox
			serverConnectionRef.updateMessages();
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

}
//#endif
