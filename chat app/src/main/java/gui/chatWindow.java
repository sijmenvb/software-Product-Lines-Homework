package gui;

import javafx.scene.control.Button;
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
public class chatWindow extends VBox {
	private final int SPACING = 5;// how much space is between the different elements.

	// font settings
	private String fontFamily = "Helvetica";
	private double fontSize = 20;

	private TextFlow textFlow;// special box to combine and display formatted (e.g. colored) text.

	/**
	 * a window providing a view of messages and an input field + send button.
	 * 
	 */
	public chatWindow() {
		this.setSpacing(SPACING);// set vertical spacing.

		// add text view with scroll bar.
		textFlow = new TextFlow();
		ScrollPane chatPane = new ScrollPane(textFlow);
		chatPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);// always show vertical scroll bar.
		chatPane.setHbarPolicy(ScrollBarPolicy.NEVER);// never show horizontal scroll bar.
		chatPane.setPrefHeight(Integer.MAX_VALUE);// make it expand when possible
		chatPane.setFitToWidth(true);// make sure the elements in the scrollPane are restricted to the width of the
										// scrollPane.

		Region bottomSpacing = new Region();// used so the buttons will not be completely be at the but will use the
											// vertical spacing.

		// create message input and send button.
		TextField textInput = new TextField();
		Button sendButton = new Button("send");
		HBox textInputContainer = new HBox(SPACING, textInput, sendButton);

		// populate the chatBox with some dummy text.
		addText("hello\n");
		addText("I am jeffrey :) i am a very good boiiiii!\n", Color.ORANGE);

		this.getChildren().addAll(chatPane, textInputContainer, bottomSpacing);// add all the elements of the UI to this
																				// VBox.
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
		Text text = new Text(contents.toString());

		text.setFill(color);// set the colour of the text

		FontWeight weight = FontWeight.NORMAL; // font weight may be used for bold text
		FontPosture posture = FontPosture.REGULAR;// font posture might be used for italics

		text.setFont(Font.font(fontFamily, weight, posture, fontSize));

		textFlow.getChildren().add(text);// add this text to the chat dialogue.
	}
}
