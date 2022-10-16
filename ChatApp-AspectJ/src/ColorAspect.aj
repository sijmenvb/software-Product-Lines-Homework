import org.json.JSONObject;

import enums.JSONKeys;
import ui.CLI;
import ui.ConsoleColors;


public aspect ColorAspect {
	
	pointcut printingMessage(CLI c, JSONObject s) : 
		execution(void *.printMessage(JSONObject)) && args(s) && target(c);
	
	void around(CLI c, JSONObject obj): printingMessage(c, obj) {
		String color = obj.getString(JSONKeys.COLOR.toString());
		if(color.equals("0x000000ff")) {
			proceed(c, obj);
		} else {
			System.out.print(ConsoleColors.CYAN);
			proceed(c, obj);
			System.out.print(ConsoleColors.RESET);
		}
	}
}
