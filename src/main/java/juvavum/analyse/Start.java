package juvavum.analyse;

import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Start {

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("g", "game", true,
				"Game type (Juvavum, Domino Juvavum, Cram), default: Juvavum");
		Option heightOption = new Option("h", "height", true,
				"Height of the game board");
		heightOption.setRequired(true);
		options.addOption(heightOption);
		Option widthOption = new Option("w", "width", true,
				"Width of the game board");
		widthOption.setRequired(true);
		options.addOption(widthOption);
		options.addOption("s", "symmetries", false, "Use symmetries");
		options.addOption("n", "normalforms", false,
				"Use normal forms (Juvavum only)");
		options.addOption("m", "misere", false, "Misere type game");
		options.addOption("f", "file", false,
				"Output result and positions to file");

		CommandLineParser parser = new BasicParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			printHelp("java -jar juvavum.jar", options, System.out);
			System.exit(1);
		}

		int h = 0;
		int w = 0;
		try {
			h = Integer.parseInt(cmd.getOptionValue('h'));
			w = Integer.parseInt(cmd.getOptionValue('w'));
		} catch (NumberFormatException e) {
			System.out.println("Please enter integers for height and width.");
			System.exit(1);
		}
		boolean misere = (cmd.hasOption('m') ? true : false);
		boolean fileOutput = (cmd.hasOption('f') ? true : false);
		boolean symmetries = (cmd.hasOption('s') ? true : false);
		boolean normalForms = (cmd.hasOption('n') ? true : false);

		if (cmd.hasOption('g')) {
			switch (cmd.getOptionValue('g').toLowerCase()
					.replaceAll("\\s+", "")) {
			case "juvavum":
				if (normalForms) {
					new JAnalyseNormalForm(h, w, misere, true, fileOutput);
				} else {
					new JAnalyse(h, w, misere, symmetries, fileOutput);
				}
				break;
			case "dominojuvavum":
				new DJAnalyse(h, w, misere, symmetries, fileOutput);
				break;
			case "cram":
				new CramAnalyse(h, w, misere, symmetries, fileOutput);
				break;
			default:
				System.out.println("Unknown game type: "
						+ cmd.getOptionValue('g'));
				System.exit(1);
			}
		} else {
			if (normalForms) {
				new JAnalyseNormalForm(h, w, misere, true, fileOutput);
			} else {
				new JAnalyse(h, w, misere, symmetries, fileOutput);
			}
		}
	}

	public static void printHelp(final String applicationName,
			final Options options, final OutputStream out) {
		final PrintWriter writer = new PrintWriter(out);
		final HelpFormatter usageFormatter = new HelpFormatter();
		usageFormatter.printHelp(applicationName, options, true);
		writer.close();
	}
}
