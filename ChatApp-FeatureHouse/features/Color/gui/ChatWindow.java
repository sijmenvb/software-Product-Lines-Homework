package gui;

import java.util.LinkedList;

import buttons.ColorButton;
import javafx.scene.Node;

//extends VBox so it is a javaFX element and can be used as such.
public class ChatWindow {
	
	private LinkedList<Node> getNodes(){
		LinkedList<Node> nodes = new LinkedList<Node>();
		nodes.add(new ColorButton().getNode());
		return nodes;
	}
}
