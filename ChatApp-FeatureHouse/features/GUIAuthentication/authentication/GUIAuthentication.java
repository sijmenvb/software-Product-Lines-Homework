
package authentication;

import javafx.scene.control.PasswordField;

import backend.ServerConnection;
import main.AuthenticationInterface;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.LoggingInterface;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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


	// calling constructor
	public GUIAuthentication() {
	}
	
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

	public void handleCredentials(String inputUsername, String inputPassword) {
		if (verifyCredential(inputUsername, inputPassword)) {
			primaryStage.setScene(nextScene);
			// populate the chatBox
			serverConnectionRef.updateMessages();
			isLoggedIn = true;
			return;
		}
		feedbackLabel.setText(String.format("(%d) username/password invalid", loginAttempts));
		loginAttempts++;
	}

	public boolean verifyCredential(String username, String password) {
		return serverConnectionRef.firstAuthentication(username, password);
	}

	public boolean userIsLoggedIn() {
		return isLoggedIn;
	}
	
	public boolean usesJavafx() {
		return usesJavafx;
	}

	public void setServerConnection(ServerConnection serverConnection) {
		serverConnectionRef = serverConnection;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;		
	}

	public void setNextScene(Scene nextScene) {
		this.nextScene = nextScene;		
	}
}
