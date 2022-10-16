package main;

import ui.CLI;

public class Main  {
	public static void main(String[] args) {
		UIInterface ui = new CLI();
		ui.start();
	}
}
