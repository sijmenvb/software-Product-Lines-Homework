package buttons;

import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import main.ButtonInterface;

public class ColorButton implements ButtonInterface {
	
	private ColorPicker colorSelector;

	@Override
	public Node getNode() {
		colorSelector = new ColorPicker(Color.BLACK);
		return colorSelector;
	}

	@Override
	public Color getColor() {
		return colorSelector.getValue();
	}
}
