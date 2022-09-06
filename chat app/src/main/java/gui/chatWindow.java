package gui;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class chatWindow extends VBox {

	public chatWindow() {
		Text t = new Text("hello");

		this.getChildren().addAll(t);
	}

}
