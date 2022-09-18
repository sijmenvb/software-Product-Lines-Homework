package gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import backend.ServerConnection;
import javafx.application.Platform;
import enums.Algorithms;
import enums.JSONKeys;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

//extends VBox so it is a javaFX element and can be used as such.
public class ChatWindow extends VBox implements PropertyChangeListener {
	private final int SPACING = 5;// how much space is between the different elements.
	private ServerConnection serverConnectionRef;

	// font settings
	private String fontFamily = "Helvetica";
	private double fontSize = 20;

	private TextFlow textFlow;// special box to combine and display formatted (e.g. colored) text.
	private ColorPicker colorSelector;

	private JSONArray currentMessages = new JSONArray();

	static Logger log = Logger.getLogger(ChatWindow.class.getName());

	/**
	 * a window providing a view of messages and an input field + send button.
	 * 
	 */
	public ChatWindow(ServerConnection serverConnection) {
		log.debug("ChatWindow created.");

		serverConnectionRef = serverConnection;

		this.setSpacing(SPACING);// set vertical spacing.

		// add text view with scroll bar.
		textFlow = new TextFlow();
		ScrollPane chatPane = new ScrollPane(textFlow);
		chatPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);// always show vertical scroll bar.
		chatPane.setHbarPolicy(ScrollBarPolicy.NEVER);// never show horizontal scroll bar.
		chatPane.setPrefHeight(Integer.MAX_VALUE);// make the chatPane expand when possible.
		chatPane.setFitToWidth(true);// make sure the elements in the scrollPane are restricted to the width of the
										// scrollPane.

		// create message input, send button and color selector and refresh button.
		final TextField textInput = new TextField();
		Button sendButton = new Button("send");
		final ComboBox<String> encryptionComboBox = new ComboBox<String>();
		encryptionComboBox.getItems().addAll(Algorithms.AES.toString(), Algorithms.REVERSE.toString());
		encryptionComboBox.setValue(Algorithms.AES.toString());
		colorSelector = new ColorPicker(Color.BLACK);
		Button refreshButton = new Button("Refresh");
		HBox textInputContainer = new HBox(SPACING, textInput, sendButton, encryptionComboBox, colorSelector,
				refreshButton);

		// make send button run the send function with the provided text and clear the
		// text input.
		sendButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				log.debug("Send button pressed.");
				send(textInput.getText(), colorSelector.getValue(), encryptionComboBox.getValue());
				textInput.clear();
			}
		});

		refreshButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				log.debug("Refresh button pressed.");
				serverConnectionRef.updateMessages();
			}
		});

		Region bottomSpacing = new Region();// used so the buttons will not be completely be at the bottom but will use
											// the vertical spacing.

		this.getChildren().addAll(chatPane, textInputContainer, bottomSpacing);// add all the elements of the UI to this
																				// VBox.

	}

	public void updateMessages(JSONArray messages) {
		Platform.runLater(new Runnable() {
		    public void run() {
		    	textFlow.getChildren().clear();
		    }
		});

		// TODO: add user name to text.

		// add all the text to the dialogue.
		for (Object object : messages) {
			if (object instanceof JSONObject) {
				JSONObject textObject = (JSONObject) object;// cast to jsonObject
				addText(textObject.getString(JSONKeys.TEXT.toString()),
						Color.web(textObject.getString(JSONKeys.COLOR.toString())));
			}
		}
	}
	

	/**
	 * adds text to the chat dialogue. does NOT add nextLines implicitly.
	 * 
	 * @param contents the text to be displayed
	 */
	private void addText(String contents) {
		addText(contents, Color.BLACK);
	}

	/**
	 * adds text to the chat dialogue. does NOT add nextLines implicitly.
	 * 
	 * @param contents the text to be displayed
	 * @param color    the color of the text as a javaFX Paint
	 */
	private void addText(String contents, Paint color) {
		final Text text = new Text(contents.toString());

		text.setFill(color);// set the color of the text.

		FontWeight weight = FontWeight.NORMAL; // font weight may be used for bold text.
		FontPosture posture = FontPosture.REGULAR;// font posture might be used for italics.

		text.setFont(Font.font(fontFamily, weight, posture, fontSize));

		//textFlow.getChildren().add(text);
		
		Platform.runLater(new Runnable() {
		    public void run() {
		    	textFlow.getChildren().add(text);
		    }
		});
		
		
		//textFlow.getChildren().add(text);// add this text to the chat dialogue.
	}

	/**
	 * function that gets run when the button is clicked.
	 * 
	 * @param text       the text from the input field
	 * @param color      the selected color.
	 * @param encryption algorithm used.
	 */
	private void send(String text, Color color, String encryption) {
		log.info(String.format("Message with text: '%s' send in color: '%s'.", text, color.toString()));
		serverConnectionRef.sendMessage(text + "\n", color, Algorithms.fromString(encryption));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		updateMessages((JSONArray) evt.getNewValue());		
	}

}
