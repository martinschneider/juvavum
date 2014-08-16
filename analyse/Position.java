package juvavum.analyse;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Collections;

import juvavum.visualisation.*;

/**
 * Verwaltung von Spielpositionen
 * 
 * @author Martin Schneider
 */
public class Position implements Comparable {
	long value;
	int h;
	int w;
	int grundy = -1;
	private BufferedWriter out;
	Position parent = null;
	Vector children = new Vector();

	final String FILENAME = "Ergebnisse.txt";

	/**
	 * @param b
	 * 			Spielbrett
	 */
	public Position(Board b) {
		value = b.flatten();
		h = b.getHeight();
		w = b.getWidth();
	}

	/**
	 * @param b
	 * 			Spielbrett
	 * @param h
	 * 			Höhe des Spielbretts
	 * @param w
	 * 			Breite des Spielbretts
	 */
	public Position(Board b, int h, int w) {
		value = b.flatten();
		this.h = b.getHeight();
		this.w = b.getWidth();
	}

	/**
	 * @param pos
	 * 			Position, die als Nachfolger hinzugefügt wird
	 */
	public void addChild(Position pos) {
		children.add(pos);
	}

	/**
	 * @return
	 * 			Liste aller Nachfolger
	 */
	public Vector getChildren() {
		return children;
	}

	/**
	 * @return Spielbrett der aktuellen Spielposition
	 */
	public Board getBoard() {
		return new Board(value, h, w);
	}

	/**
	 * @param p
	 * 			Spielposition
	 * @return
	 * 			true, wenn die übergebene Position unter den Nachfolgern vorkommt 
	 */
	public boolean hasChildren(Position p) {
		long value = p.getValue();
		for (Enumeration e1 = children.elements(); e1.hasMoreElements();) {
			if (((Position) e1.nextElement()).getValue() == value)
				return true;
		}
		return false;
	}

	/**
	 * @return
	 * 			Höhe des Spielbretts
	 */
	public int getHeight() {
		return h;
	}

	/**
	 * @return
	 * 			Breite des Spielbretts
	 */
	public int getWidth() {
		return w;
	}

	// TODO jede Position darf nur einmal gezählt werden!
	/**
	 * @return
	 * 			Anzahl aller Nachfolger zurück
	 */
	public int succCount() {
		if (children.isEmpty())
			return 0;
		int count = 0;
		for (Enumeration e1 = children.elements(); e1.hasMoreElements();) {
			count += ((Position) e1.nextElement()).succCount();
		}
		return children.size() + count;
	}

	/**
	 * @return
	 * 			Anzahl der direkten Nachfolger
	 */
	public int directSuccCount() {
		return children.size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return value + " | " + grundy + " | " + children.size();
	}

	/**
	 * Setzt den Grundywert der Position
	 * 
	 * @param grundy
	 * 		Grundywert
	 */
	public void setGrundy(int grundy) {
		this.grundy = grundy;
	}

	/**
	 * Gibt den Grundywert der Position im normalen Spiel zurück
	 * 
	 * @param fileOutput
	 * 			0 - alles ausgeben, 1 - Positionen, mit Grundywert 0 ausgeben, 2 - nichts ausgeben
	 * @return
	 * 			Grundywert
	 */
	public int getGrundy(int fileOutput) {
		if (grundy != -1)
			return grundy;
		if (children.isEmpty()) { // Zielecke
			this.grundy = 0;
			if (fileOutput == 0 || (fileOutput == 1 && grundy == 0))
				outputFile(false);
			return 0;
		} else {
			int i = 0;
			int grundy = -1;
			while (grundy == -1) {
				boolean found = false;
				for (Enumeration e1 = children.elements(); e1.hasMoreElements();) {
					if (((Position) e1.nextElement()).getGrundy(fileOutput) == i) {
						found = true;
						break;
					}
				}
				if (!found)
					grundy = i;
				i++;
			}
			this.grundy = grundy;
			if (fileOutput == 0 || (fileOutput == 1 && grundy == 0))
				outputFile(false);
			return grundy;
		}
	}

	/**
	 * Gibt den Grundywert der Position im Misere-Spiel zurück
	 * 
	 * @param fileOutput
	 * 			0 - alles ausgeben, 1 - Positionen, mit Grundywert 0 ausgeben, 2 - nichts ausgeben
	 * @return
	 * 			Grundywert
	 */
	public int getGrundyMisere(int fileOutput) {
		if (grundy != -1)
			return grundy;
		if (children.isEmpty() ) { // Zielecke
			this.grundy = 1;
			if (fileOutput == 0 || (fileOutput == 1 && grundy == 0))
				outputFile(true);
			return 1;
		} else {
			int i = 0;
			int grundy = -1;
			while (grundy == -1) {
				boolean found = false;
				for (Enumeration e1 = children.elements(); e1.hasMoreElements();) {
					if (((Position) e1.nextElement()).getGrundyMisere(fileOutput) == i) {
						found = true;
						break;
					}
				}
				if (!found)
					grundy = i;
				i++;
			}
			this.grundy = grundy;
			if (fileOutput == 0 || (fileOutput == 1 && grundy == 0))
				outputFile(true);
			return grundy;
		}
	}

	/**
	 * @return
	 * 			Binärrepräsentation des Spielbretts
	 */
	public long getValue() {
		return value;
	}

	/**
	 * sortiert die Liste der Nachfolger
	 */
	public void sortChildren() {
		Collections.sort(children);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		long cValue = ((Position) o).getValue();
		if (cValue > value)
			return -1;
		if (cValue < value)
			return 1;

		return 0;
	}

	/**
	 * @return
	 * 			durchschnittlicher Verzweigungsfaktor
	 */
	public double getBranchingFactor() {
		return getBranching()[0] / getBranching()[1];
	}

	/**
	 * @return
	 * 			Array zur Berechnung des durchschnittlichen Verzweigungsfaktors
	 */
	public int[] getBranching() {
		int[] ret = new int[2];
		int succ = 0;
		int nodeCount = 1;
		for (Enumeration e1 = children.elements(); e1.hasMoreElements();) {
			succ += ((Position) e1.nextElement()).children.size();
			nodeCount++;
		}
		ret[0] = succ;
		ret[1] = nodeCount;
		return ret;
	}

	/**
	 * @param misere
	 * 			true, wenn Misere-Form
	 * @return
	 * 			Nachfolger mit Grundywert 0
	 */
	private Vector getGoodSucc(boolean misere) {
		Vector goodSucc = new Vector();
		if (!misere) {
			for (Enumeration e1 = this.children.elements(); e1
					.hasMoreElements();) {
				Position posTmp = ((Position) e1.nextElement());
				if (posTmp.getGrundy(2) == 0)
					goodSucc.add(posTmp.getValue());
			}
			return goodSucc;
		} else {
			for (Enumeration e1 = this.children.elements(); e1
					.hasMoreElements();) {
				Position posTmp = ((Position) e1.nextElement());
				if (posTmp.getGrundyMisere(2) == 0)
					goodSucc.add(posTmp.getValue());
			}
			return goodSucc;
		}
	}

	/**
	 * Ausgabe in Textdatei
	 * 
	 * @param misere
	 * 			true, wenn Misere-Form
	 */
	private void outputFile(boolean misere) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(FILENAME, true)));
		} catch (FileNotFoundException e) {
			System.out.println(e);
		}
		int i = 0;
		Vector goodSucc = null;
		if (grundy > 0) {
			goodSucc = this.getGoodSucc(misere);
		}
		try {
			if (grundy > 0 && !goodSucc.isEmpty()) {
				String ret = "";
				out.write(value + "\t" + grundy + "\t" + goodSucc.toString());
			} else
				out.write(value + "\t" + grundy);
			out.newLine();
			out.close();
		} catch (IOException e1) {
			System.out.println(e1.getMessage());
		}
	}

	/**
	 * Gibt den Spielegraph aus.
	 * Erstellt eine XML-Datei des Spielegraphen und gibt diesen
	 * mit Hilfe des prefuse-Paktes aus
	 * TODO: in einer zukünftigen Version, köönte man den Graphen ev.
	 * ohne den Umweg einer XML-Datei erzeugen
	 * 
	 * @param filename
	 * 			Dateiname
	 * @param misere
	 * 			true, wenn Misere-Form
	 */
	public void showGraph(String filename, boolean misere) {
		int maxGrundy = makeXML(filename, misere);
		new GameGraph(filename, maxGrundy);
	}

	/**
	 * Erstellt die XML-Datei des Spielegraphen (graphml).
	 * 
	 * @param filename
	 * 			Dateiname
	 * @param misere
	 * 			true, wenn Misere-Form
	 * @return
	 * 			maximalen Grundywert (zur späteren Färbung des Graphen)
	 */
	public int makeXML(String filename, boolean misere) {
		BufferedWriter out = null;
		int maxGrundy = 0;
		try {
			new File(filename).delete();
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filename, true)));
		} catch (FileNotFoundException e) {
			System.out.println(e);
		}
		try {
			// Header schreiben
			out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			out.newLine();
			out
					.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">");
			out.newLine();
			out.write("<graph edgedefault=\"directed\">");
			out.newLine();
			out
					.write("<key id=\"description\" for=\"node\" attr.name=\"description\" attr.type=\"string\"/>");
			out.newLine();
			out
					.write("<key id=\"grundy\" for=\"node\" attr.name=\"grundy\" attr.type=\"integer\"/>");
			out.newLine();
			out
					.write("<key id=\"postype\" for=\"node\" attr.name=\"postype\" attr.type=\"integer\"/>");
			out.newLine();
			out
					.write("<key id=\"winedge\" for=\"node\" attr.name=\"winedge\" attr.type=\"boolean\"/>");
			out.newLine();

			// Daten schreiben
			maxGrundy = makeXML2(filename, out, maxGrundy, new Vector(), this,
					misere);

			// Footer schreiben
			out.newLine();
			out.write("</graph>");
			out.newLine();
			out.write("</graphml>");
			out.newLine();

			out.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return maxGrundy;
	}

	/**
	 * @param filename
	 * 			Dateiname
	 * @param out
	 * 			BufferedWriter
	 * @param maxGrundy
	 * 			maximaler Grundywert
	 * @param saved
	 * 			Liste der Positionen
	 * @param pos
	 * 			Position
	 * @param misere
	 * 			true, wenn Misere-Form
	 * @return
	 */
	private int makeXML2(String filename, BufferedWriter out, int maxGrundy,
			Vector saved, Position pos, boolean misere) {
		try {
			int grundy;
			int directsucc;
			int posType; // 1 für Startecke, 3 für Zielecken, 2 für alle
			// anderen
			String description;
			long id;
			long parent;
			if (!misere)
				grundy = pos.getGrundy(2);
			else
				grundy = pos.getGrundyMisere(2);

			description = pos.getBoard().toString() + "\nGrundy: " + grundy;
			id = pos.getValue();
			directsucc = pos.directSuccCount();
			if (directsucc > 0)
				posType = 2;
			else
				posType = 3;
			if (pos.getValue() == 0)
				posType = 1;

			boolean isWinedge = true;
			if (grundy > 0)
				isWinedge = false;

			// Maximale Werte für Grundywert aktualisieren.
			// Dieser wird später zur Färbung des Graphen verwendet.
			// TODO: Das lässt sich sicher eleganter lösen.
			if (grundy > maxGrundy)
				maxGrundy = grundy;

			if (!saved.contains(new Long(id))) { // Ecken nicht doppelt
				// hinzufügen
				out.newLine();
				out.write("<node id=\"" + id + "\">");
				out.newLine();
				out.write("  <data key=\"description\">" + description
						+ "</data>");
				out.newLine();
				out.write("  <data key=\"grundy\">" + grundy + "</data>");
				out.newLine();
				out.write("  <data key=\"postype\">" + posType + "</data>");
				out.newLine();
				out.write("  <data key=\"winedge\">" + isWinedge + "</data>");
				out.newLine();
				out.write("</node>");
				out.newLine();
				saved.add(id);
			}
			parent = id;
			for (Enumeration e1 = pos.getChildren().elements(); e1
					.hasMoreElements();) {
				Position nextPos = (Position) e1.nextElement();
				id = nextPos.getValue();
				out.write("<edge source=\"" + parent + "\" target=\"" + id
						+ "\"></edge>");
				out.newLine();
				maxGrundy = makeXML2(filename, out, maxGrundy, saved, nextPos,
						misere);
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return maxGrundy;
	}
}