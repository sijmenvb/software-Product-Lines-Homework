package gui;

import java.util.LinkedList;

import buttons.ColorButton;
import javafx.scene.Node;
import main.ButtonInterface;

//extends VBox so it is a javaFX element and can be used as such.
public class ChatWindow {
	
	private LinkedList<ButtonInterface> buttonInterfaceList;

	private void setButtonInterfaceList(){
		buttonInterfaceList = new LinkedList<ButtonInterface>();
		buttonInterfaceList.add(new ColorButton());
	}
}
