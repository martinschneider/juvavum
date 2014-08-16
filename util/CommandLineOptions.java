package juvavum.util;

import java.util.Vector;

/**
 * Klasse zur Verarbeitung von Kommandozeilenparametern
 * 
 * @author Martin Schneider
 */
public class CommandLineOptions {
	Vector options;

	/**
	 * @param args
	 */
	public CommandLineOptions(String[] args) {
		options = new Vector();
		for (int i = 0; i < args.length; i++) {
			options.add(args[i]);
		}
	}

	/**
	 * @param option
	 * @return
	 */
	public boolean hasOption(String option) {
		return options.contains(option);
	}
}
