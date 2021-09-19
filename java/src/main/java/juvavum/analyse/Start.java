package juvavum.analyse;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.oath.halodb.HaloDB;
import com.oath.halodb.HaloDBException;
import com.oath.halodb.HaloDBOptions;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
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
    options.addOption(
        "g", "game", true, "Game type (Juvavum, Domino Juvavum, Cram), default: Juvavum");
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
    options.addOption(
        "c",
        "components",
        false,
        "Break game into components and use the Grundy-Sprague theorem for sums of games");
    options.addOption("d", "database", true, "Export winning moves to database");

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
    String dbPath = cmd.getOptionValue('d');

    Analysis analysis = null;

    String game;
    if (!cmd.hasOption('g')) {
      game = "juvavum";
    } else {
      game = cmd.getOptionValue('g').toLowerCase().replaceAll("\\s+", "");
    }
    if (cmd.hasOption('G') && !game.equals("cram")) {
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
    if (components && !graphBased) {
      exitWithError("Normal forms can only be considered for game type Juvavum (-g juvavum)");
    }
    if (symmetries && graphBased) {
      exitWithError(
          "Symmetries can not be considered for graph-based analysis. Use isomorphisms (-i) and/or components (-c) instead.");
    }

    switch (game) {
      case "juv":
      case "juvavum":
        if (normalForms) {
          analysis = new JUVAnalysisNormalForm(new Board(h, w), misere);
        } else if (symmetries) {
          analysis = new JUVAnalysisSymm(new Board(h, w), misere);
        } else {
          analysis = new JUVAnalysis(new Board(h, w), misere);
        }
        break;
      case "djuv":
      case "dominojuvavum":
        if (symmetries) {
          analysis = new DJUVAnalysisSymm(new Board(h, w), misere);
        } else {
          analysis = new DJUVAnalysis(new Board(h, w), misere);
        }
        break;
      case "cram":
        if (!misere && !symmetries && !graphBased && (h == 1 || w == 1)) {
          analysis = new LCRAMAnalysis(h, w);
          break;
        }
        if (graphBased) {
          analysis = new GraphCramAnalysis(new Board(h, w), misere, isomorphisms, components);
        } else {
          if (symmetries) {
            analysis = new CRAMAnalysisSymm(new Board(h, w), misere);
          } else {
            analysis = new CRAMAnalysis(new Board(h, w), misere);
          }
        }
        break;
      default:
        System.out.println("Unknown game type: " + cmd.getOptionValue('g'));
        System.exit(1);
    }
    analysis.analyse();
    if (database) {
      HaloDB db = null;
      try {
        System.out.println("Writing results to HaloDB at " + dbPath);
        db = HaloDB.open(dbPath, new HaloDBOptions());
        for (Map.Entry<Long, Set<Long>> entry : analysis.winningMoves().entrySet()) {
          byte[] value = new byte[] {};
          for (Long succ : entry.getValue()) {
            value = Bytes.concat(value, Longs.toByteArray(succ));
          }
          db.put(buildKey(game, w, h, misere, entry.getKey()), value);
        }
        db.close();
      } catch (HaloDBException e) {
        System.err.println("Error " + e.getMessage());
      }
    }
  }

  private static byte[] buildKey(String gameName, int w, int h, boolean misere, long position) {
    int game = 0;
    switch (gameName) {
      case "juv":
      case "juvavum":
        game = 1;
        break;
      case "djuv":
      case "dominojuvavum":
        game = 2;
        break;
      case "cram":
        game = 3;
    }
    return Bytes.concat(
        new byte[] {(byte) game},
        new byte[] {(byte) h},
        new byte[] {(byte) w},
        new byte[] {(byte) (misere ? 1 : 0)},
        Longs.toByteArray(position));
  }

  public static void exitWithError(String message) {
    System.out.println("Error: " + message);
    System.exit(1);
  }

  public static void printHelp(
      final String applicationName, final Options options, final OutputStream out) {
    final PrintWriter writer = new PrintWriter(out);
    final HelpFormatter usageFormatter = new HelpFormatter();
    usageFormatter.printHelp(applicationName, options, true);
    writer.close();
  }
}
