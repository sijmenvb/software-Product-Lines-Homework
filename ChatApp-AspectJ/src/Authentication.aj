import java.util.Scanner;

import ui.CLI;

public aspect Authentication {

	pointcut auth(CLI c) : execution(boolean *.authenticate()) && target(c);
	
	boolean around(CLI c): auth(c) {
		Scanner consoleInput = new Scanner(System.in);
		boolean loggedIn = false;
		while (!loggedIn) {
			System.out.println("username:");
			String username = consoleInput.nextLine();
			
			
			System.out.println("password:");
			String password = consoleInput.nextLine();
			if (c.verifyCredential(username, password)) {
				loggedIn = true;
			}
			else {				
				System.out.println("username/password invalid");
			}
		}
		consoleInput.close();
		return true;
	}
}
