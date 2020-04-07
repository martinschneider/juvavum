package juvavum.analyse;

import java.io.OutputStream;
import java.io.PrintWriter;
import juvavum.graph.DBCramAnalysis;
import juvavum.graph.GraphCramAnalysis;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Main program
 *
 * @author Martin Schneider, mart.schneider@gmail.com
 */
public class Start {

  public static void main(String[] args) {
    Options options = new Options();
    options.addOption("g", "game", true,
        "Game type (Juvavum, Domino Juvavum, Cram), default: Juvavum");
    options.addOption("G", "graph-analysis", false, "Use graph based analysis (Cram only)");
    Option heightOption = new Option("h", "height", true, "Height of the game board");
    heightOption.setRequired(true);
    options.addOption(heightOption);
    Option widthOption = new Option("w", "width", true, "Width of the game board");
    widthOption.setRequired(true);
    options.addOption(widthOption);
    options.addOption("s", "symmetries", false, "Use symmetries");
    options.addOption("i", "isomorphisms", false, "Use graph isomorphisms");
    options.addOption("n", "normalforms", false, "Use normal forms (Juvavum only)");
    options.addOption("m", "misere", false, "Misere type game");
    options.addOption("c", "components", false,
        "Break game into components and use the Grundy-Sprague theorem for sums of games");
    options.addOption("d", "database", false,
        "Store results in a local MapDB (only for CRAM, uses Graph-based analysis with isomorphisms and components)");

    CommandLineParser parser = new DefaultParser();
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
      exitWithError("Please enter integers for height and width.");
    }
    boolean misere = (cmd.hasOption('m') ? true : false);
    boolean normalForms = (cmd.hasOption('n') ? true : false);
    boolean graphBased = (cmd.hasOption('G') ? true : false);
    boolean symmetries = (cmd.hasOption('s') ? true : false);
    boolean isomorphisms = (cmd.hasOption('i') ? true : false);
    boolean components = (cmd.hasOption('c') ? true : false);
    boolean database = (cmd.hasOption('d') ? true : false);

    String game = cmd.getOptionValue('g').toLowerCase().replaceAll("\\s+", "");
    if (game.equals("juv")) {
      game = "juvavum";
    }
    if (game.equals("djuv")) {
      game = "dominojuvavum";
    }
    if (graphBased && !game.equals("cram")) {
      exitWithError("Graph analysis is only available for game type Cram (-g cram)");
    }
    if (isomorphisms && !graphBased) {
      exitWithError("Isomorphisms can only be considered during graph-analysis (-G)");
    }
    if (components && !graphBased) {
      exitWithError("Components can only be considered during graph-analysis (-G)");
    }
    if (components && misere) {
      exitWithError(
          "Components can only be considered for the normal form of Cram, not for misere (the sum theorem does not apply)");
    }
    if (symmetries && graphBased) {
      exitWithError(
          "Symmetries can not be considered for graph-based analysis. Use isomorphisms (-i) and/or components (-c) instead.");
    }
    if (database && !game.equals("cram")) {
      exitWithError("Database export is only supported for Cram (-g cram)");
    }
    if (normalForms && !game.equals("juvavum")) {
      exitWithError("Normal forms can only be considered for game type Juvavum (-g juvavum)");
    }

    if (cmd.hasOption('g')) {
      switch (game) {
        case "juvavum":
          if (normalForms) {
            new JUVAnalysisNormalForm(new Board(h, w), misere).analyse();
          } else if (symmetries) {
            new JUVAnalysisSymm(new Board(h, w), misere).analyse();
          } else {
            new JUVAnalysis(new Board(h, w), misere).analyse();
          }
          break;
        case "dominojuvavum":
          if (symmetries) {
            new DJUVAnalysisSymm(new Board(h, w), misere).analyse();
          } else {
            new DJUVAnalysis(new Board(h, w), misere).analyse();
          }
          break;
        case "cram":
          if (database) {
            new DBCramAnalysis(new Board(h, w), misere).analyse();
            break;
          }
          if (!misere && !symmetries && !graphBased && (h == 1 || w == 1)) {
            new LCRAMAnalysis(h, w).analyse();
            break;
          }
          if (graphBased) {
            new GraphCramAnalysis(new Board(h, w), misere, isomorphisms, components).analyse();
          } else {
            if (symmetries) {
              new CRAMAnalysisSymm(new Board(h, w), misere).analyse();
            } else {
              new CRAMAnalysis(new Board(h, w), misere).analyse();
            }
          }
          break;
        default:
          System.out.println("Unknown game type: " + cmd.getOptionValue('g'));
          System.exit(1);
      }
    }
  }

  public static void exitWithError(String message) {
    System.out.println("Error: " + message);
    System.exit(1);
  }

  public static void printHelp(final String applicationName, final Options options,
      final OutputStream out) {
    final PrintWriter writer = new PrintWriter(out);
    final HelpFormatter usageFormatter = new HelpFormatter();
    usageFormatter.printHelp(applicationName, options, true);
    writer.close();
  }
}
