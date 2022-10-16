package main;

import ui.CLI;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class Main  {
	public static void main(String[] args) {
		
		BasicConfigurator.configure();
		
		UIInterface ui = new CLI();
		ui.start();
	}
}
