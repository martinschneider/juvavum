package juvavum.analyse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import juvavum.gui.*;

import juvavum.util.List;
import juvavum.util.Timing;

/**
 * Analyseprogramm f�r Juvavum (verwendet Normalformen)
 * 
 * @author Martin Schneider
 */
public class JAnalyseNormalForm {

	public List pos = new List();

	private boolean misere;

	private boolean symmetrien;

	private boolean db;

	private int w;

	private int h;

	private Board b;

	private Board bHelp;

	private Timing timer;

	private boolean graph;

	private boolean textOnly;

	private int fileOutput;

	final String FILENAME = "Ergebnisse.txt";

	/**
	 * @param h
	 * 			H�he des Spielfelds
	 * @param w
	 * 			Breite des Spielfelds
	 * @param misere
	 * 			wenn Misere-Spiel untersucht werden soll?
	 * @param symmetrien
	 * 			nur symmetrische Positionen betrachten?
	 * @param textOnly
	 * 			keinen Spielbaum ausgeben
	 * @param fileOutput
	 * 			Ergebnisse in Textdatei speichern (0 - alle Positionen, 1 - nur Positionen mit Grundywert 1, 2- nichts speichern)
	 * @param graph
	 */
	public JAnalyseNormalForm(int h, int w, boolean misere, boolean symmetrien,
			boolean textOnly, int fileOutput, boolean graph) {
		Board b = new Board(h, w);
		new JAnalyseNormalForm(b, misere, symmetrien, textOnly, fileOutput,
				graph);
	}

	/**
	 * @param b
	 * 			zu analysierendes Spielbrett
	 * @param misere
	 * 			wenn Misere-Spiel untersucht werden soll?
	 * @param symmetrien
	 * 			nur symmetrische Positionen betrachten?
	 * @param textOnly
	 * 			keinen Spielbaum ausgeben
	 * @param fileOutput
	 * 			Ergebnisse in Textdatei speichern (0 - alle Positionen, 1 - nur Positionen mit Grundywert 1, 2- nichts speichern)
	 * @param graph
	 */
	public JAnalyseNormalForm(Board b, boolean misere, boolean symmetrien,
			boolean textOnly, int fileOutput, boolean graph) {
		this.b = b;
		this.misere = misere;
		this.symmetrien = symmetrien;
		this.h = b.getHeight();
		this.w = b.getWidth();
		this.fileOutput = fileOutput;
		if (fileOutput < 2)
			printHeaderToFile();
		this.textOnly = textOnly;
		this.graph = graph;

		if (!misere)
			System.out.println("JUV(" + h + "x" + w + ")");
		else
			System.out.println("JUVM(" + h + "x" + w + ")");

		// Timer erzeugen und starten
		timer = new Timing();
		timer.begin();

		// Spielfeld und Liste der Positionen erzeugen
		pos = new List();
		pos.add(new Position(b));

		// Analyse starten
		move(b, 0);

		// Ergebnisse ausgeben
		printResults();
	}

	/**
	 * Findet alle Nachfolger einer Spielposition und f�gt Verweise zur Liste
	 * aller Positionen hinzu.
	 * 
	 * @param b
	 *            Spielfeld
	 * @param caller
	 *            Nummer der Ausgangsposition in der pos-Liste
	 */
	private void move(Board b, int caller) {
		moveRow(b, caller);
		moveColumn(b, caller);
	}

	/**
	 * Findet alle Nachfolger, die durch einen Spaltenzug erreichbar sind.
	 * 
	 * @param b
	 *            Spielfeld
	 * @param caller
	 *            Nummer der Ausgangsposition in der pos-Liste
	 */
	private void moveColumn(Board b, int caller) {
		int posNr = 0;
		Position tmp = null;
		int i, j;
		int foundPos = -1;
		i = j = 1;
		while (i <= b.getWidth()) {
			j = 1;
			while (j <= b.getHeight()) {
				if (b.isFree(i, j)) {
					// free pos found
					b.set(i, j);
					foundPos = pos.search(b.flatten());
					tmp = new Position(b);
					if (symmetrien && foundPos == -1) {
						foundPos = findSymmetricPosition(b);
					}
					if (foundPos == -1) {
						if (!pos.get(caller).hasChildren(tmp)) {
							posNr = pos.add(tmp);
							pos.get(caller).addChild(pos.get(posNr));
							moveCurrentColumn(b, caller, i);
							move(b, posNr);

						}
					} else {
						if (!pos.get(caller).hasChildren(pos.get(foundPos)))
							pos.get(caller).addChild(pos.get(foundPos));
						moveCurrentColumn(b, caller, i);
					}
					b.clear(i, j);
				}
				j++;
			}
			i++;
		}
	}

	/**
	 * Findet alle Nachfolger, die durch einen Zeilenzug erreichbar sind.
	 * 
	 * @param b
	 *            Spielfeld
	 * @param caller
	 *            Nummer der Ausgangsposition in der pos-Liste
	 */
	private void moveRow(Board b, int caller) {
		int posNr = 0;
		Position tmp = null;
		int i, j;
		int foundPos = -1;
		i = 1;
		j = 1;
		while (j <= b.getHeight()) {
			i = 1;
			while (i <= b.getWidth()) {
				if (b.isFree(i, j)) {
					// free pos found
					b.set(i, j);
					foundPos = pos.search(b.flatten());
					tmp = new Position(b);
					if (symmetrien && foundPos == -1) {
						foundPos = findSymmetricPosition(b);
					}
					if (foundPos == -1) {
						if (!pos.get(caller).hasChildren(tmp)) {
							posNr = pos.add(tmp);
							pos.get(caller).addChild(pos.get(posNr));

							moveCurrentRow(b, caller, j);
							move(b, posNr);
						}
					} else {
						if (!pos.get(caller).hasChildren(pos.get(foundPos)))
							pos.get(caller).addChild(pos.get(foundPos));
						moveCurrentRow(b, caller, j);
					}
					b.clear(i, j);
				}
				i++;
			}
			j++;
		}
	}

	/**
	 * Findet Spaltenz�ge mit weiteren Dominos (in der gleichen Zeile).
	 * 
	 * @param b
	 *            Spielfeld
	 * @param caller
	 *            Nummer der Ausgangsposition in der pos-Liste
	 * @param colNr
	 *            Spaltennummer
	 */
	private void moveCurrentColumn(Board b, int caller, int colNr) {
		Position tmp = null;
		int j = 0;
		int foundPos = -1;
		int posNr = 0;
		while (j <= b.getHeight()) {
			if (b.isFree(colNr, j)) {
				// free pos found
				b.set(colNr, j);
				tmp = new Position(b);
				foundPos = pos.search(b.flatten());
				if (symmetrien && foundPos == -1) {
					foundPos = findSymmetricPosition(b);
				}
				if (foundPos == -1) {
					if (!pos.get(caller).hasChildren(tmp)) {
						posNr = pos.add(tmp);
						pos.get(caller).addChild(pos.get(posNr));

						moveCurrentColumn(b, caller, colNr);
						move(b, posNr);
					}
				} else {
					if (!pos.get(caller).hasChildren(pos.get(foundPos)))
						pos.get(caller).addChild(pos.get(foundPos));
					moveCurrentColumn(b, caller, colNr);
				}
				b.clear(colNr, j);
			}
			j++;
		}
	}

	/**
	 * Findet Zeilenz�ge mit weiteren Dominos (in der gleichen Spalte).
	 * 
	 * @param b
	 *            Spielfeld
	 * @param caller
	 *            Nummer der Ausgangsposition in der pos-Liste
	 * @param rowNr
	 *            Zeilennummer
	 */
	private void moveCurrentRow(Board b, int caller, int rowNr) {
		int j = 0;
		int posNr = 0;
		int foundPos = -1;
		Position tmp = null;
		while (j <= b.getWidth()) {
			if (b.isFree(j, rowNr)) {
				// free pos found
				b.set(j, rowNr);
				foundPos = pos.search(b.flatten());
				tmp = new Position(b);
				if (symmetrien && foundPos == -1) {
					foundPos = findSymmetricPosition(b);
				}
				if (foundPos == -1) {
					if (!pos.get(caller).hasChildren(tmp)) {
						posNr = pos.add(tmp);
						pos.get(caller).addChild(pos.get(posNr));
						moveCurrentRow(b, caller, rowNr);
						move(b, posNr);
					}
				} else {
					if (!pos.get(caller).hasChildren(pos.get(foundPos)))
						pos.get(caller).addChild(pos.get(foundPos));
					moveCurrentRow(b, caller, rowNr);
				}
				b.clear(j, rowNr);
			}
			j++;
		}
	}

	/**
	 * Sucht ob zu einer Spielposition symmetrische Positionen bereits in der
	 * Liste vorkommen.
	 * 
	 * @param b
	 *            Spielposition
	 * @return Nummer der Position (-1, falls nicht gefunden)
	 */
	public int findSymmetricPosition(Board b) {
		int foundPos = -1;
		bHelp = new Board(b);
		bHelp.makeNormalForm();
		foundPos = pos.search(bHelp.flatten());
		if (foundPos == -1) {
			b.flipud();
			foundPos = pos.search(b.flatten());
			b.flipud();
		}
		if (foundPos == -1) {
			b.fliplr();
			foundPos = pos.search(b.flatten());
			b.fliplr();
		}
		if (foundPos == -1) {
			b.rotate180();
			foundPos = pos.search(b.flatten());
			b.rotate180();
		}
		if (h == w) {
			if (foundPos == -1) {
				b.rotate90();
				foundPos = pos.search(b.flatten());
				b.rotate270();
			}
			if (foundPos == -1) {
				b.rotate270();
				foundPos = pos.search(b.flatten());
				b.rotate90();
			}
			if (foundPos == -1) {
				b.flipd1();
				foundPos = pos.search(b.flatten());
				b.flipd1();
			}
			if (foundPos == -1) {
				b.flipd2();
				foundPos = pos.search(b.flatten());
				b.flipd2();
			}
		}
		return foundPos;
	}

	/**
	 * Gibt die Ergebnisse der Analyse aus (als Text, grafisch, in Datei...)
	 */
	private void printResults() {
		boolean sortChildren = false;
		if (sortChildren) {
			for (int i = 0; i < pos.getSize(); i++)
				pos.get(i).sortChildren();
		}
		int grundyValue = 0;
		if (misere) 
			grundyValue = pos.get(0).getGrundyMisere(fileOutput);
		else
			grundyValue = pos.get(0).getGrundy(fileOutput);
		System.out.print("Der Grundywert der Startposition ist " + grundyValue
				+ ". Das hei�t, es kann stets der ");
		if (grundyValue > 0)
			System.out.print("1. ");
		else
			System.out.print("2. ");
		System.out.println("Spieler gewinnen.");
		System.out.print("Anzahl der Spielpositionen (Normalformen)");

		int posCount = pos.getSize();
		System.out.println(": " + posCount);
		int help = 0;
		for (int i = 0; i < pos.getSize(); i++)
			help += pos.get(i).directSuccCount();
		double avgBranching = (double) help / pos.getSize();

		timer.end();
		String time = timer.getElapsedTimeString();
		System.out.println("Dauer der Berechnung: " + time);

		System.out.println("Durchschnittlicher Verzweigungsfaktor: "
				+ Math.round(avgBranching * 100.) / 100. + "\n");

		if (!textOnly)
			new TreeGUI((Position) pos.get(0), new ResultsGUI(h, w, misere,
					false, true, grundyValue, posCount, avgBranching, time));
		if (fileOutput < 2) {
			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(FILENAME, true)));
			} catch (FileNotFoundException e) {
				System.out.println(e);
			}
			try {
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat formater = new SimpleDateFormat();
				out.write("Ende der Berechnung: "
						+ formater.format(cal.getTime()));
				out.newLine();
				out.newLine();
				out.close();
			} catch (IOException e) {
				System.out.println(e);
			}
			File file = new File(FILENAME);
			if (!misere)
				file.renameTo(new File("JUVNormal[" + h + "x" + w + "].txt"));
			else
				file.renameTo(new File("JUVMNormal[" + h + "x" + w + "].txt"));
		}
		if (graph) 
			if (!misere)
				pos.get(0).showGraph("JUVNormal[" + h + "x" + w
								+ "].graphml",false);
			else
				pos.get(0).showGraph("JUVMNormal[" + h + "x" + w
						+ "].graphml",true);
	}

	/**
	 * Header der Ergebnisdatei schreiben
	 */
	private void printHeaderToFile() {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(FILENAME, true)));
		} catch (FileNotFoundException e) {
			System.out.println(e);
		}
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat formater = new SimpleDateFormat();
			out.write(formater.format(cal.getTime()));
			out.newLine();
			if (!misere)
				out.write("Analyse der Spielposition " + b.flatten()
						+ " von JUV(" + b.getHeight() + "," + b.getWidth()
						+ ")");
			else
				out.write("Analyse der Spielposition " + b.flatten()
						+ " von JUVM(" + b.getHeight() + "," + b.getWidth()
						+ ")");
			out.newLine();
			out.write("Pos.\tGrundy");
			if (fileOutput == 1)
				out
						.write(" (es werden nur Positionen mit Grundywert=0 angezeigt)");
			out.newLine();
			out.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}