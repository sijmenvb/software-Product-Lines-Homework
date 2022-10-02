package gui;

import javafx.scene.control.PasswordField;

import backend.ServerConnection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.LoggingInterface;
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

	private LoggingInterface logger;

	// calling constructor
	public Authentication(Stage primaryStage, Scene nextScene, ServerConnection serverConnection, LoggingInterface logger) {
		this.logger = logger;
		this.logger.debug(this.getClass().getName(), "Authentication window created.");
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
			logger.debug(this.getClass().getName(), "Credentials of the user successfully verified.");
			return;
		}
		feedbackLabel.setText(String.format("(%d) username/password invalid", loginAttempts));
		loginAttempts++;
		logger.debug(this.getClass().getName(), "Failed credentials verification attempt.");
	}

}