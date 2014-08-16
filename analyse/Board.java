package juvavum.analyse;

import juvavum.util.SimpleGraph;
import juvavum.util.Vertex;

import java.util.HashSet;
import java.io.*;

/**
 * Repräsentation eines Spielfeld.
 * 
 * Die Klasse Board stellt ein Juvavum-Spielfeld dar. Sie beinhaltet die Größe
 * des Spielbretts (width, height) sowie die Belegung der einzelnen Felder. Die
 * Klasse kann sowohl zur Analyse von Juvavum- als auch Domino-Juvavum verwendet
 * werden.
 *
 * @author Martin Schneider
 */
public class Board {

	private boolean[][] board;

	private int h;

	private int w;

	/**
	 * Erstellt ein leeres Spielfeld-Objekt (alle Felder werden als frei
	 * initialisiert).
	 * 
	 * @param w
	 *            Breite des Spielfelds
	 * @param h
	 *            Höhe des Spielfelds
	 */
	public Board(int h, int w) {
		if (h * w > 63)
			throw new IllegalArgumentException(
					"Spielfelder mit mehr als 63 Feldern sind in dieser Version noch nicht möglich.");
		this.board = new boolean[w][h];
		this.h = h;
		this.w = w;
		fill();
	}

	/**
	 * Erstellt ein Spielfeld-Objekt aus der entsprechenden Binärkodierung.
	 * Dabei wird der übergebene integer-Wert in eine Binärfolge umgewandelt und
	 * die Felder des Spielbretts entsprechend der Werte dieser Folge
	 * zeilenweise gesetzt.
	 * 
	 * z.B.: 39 --> 1*2^0 + 1*2^1 + 1*2^2 + 1*2^5--> 1 1 1 0 0 1 0 0 0.
	 * 
	 * Sollte die Binärkodierung ein ungültigen Wert für die übergebene
	 * Spielfeldgröße sein, wird eine Exception geworfen.
	 * 
	 * @param binary
	 *            Binärkodierung des Spielfelds (als Dezimalzahl)
	 * @param w
	 *            Breite des Spielfelds
	 * @param h
	 *            Höhe des Spielfelds
	 */
	public Board(long binary, int h, int w) {
		this.h = h;
		this.w = w;

		int x = 0;
		int y = 0;
		this.board = new boolean[w][h];
		String z = decimalToBinary(binary);
		int j = 0;
		for (int i = z.length() - 1; i >= 0; i--) {
			x = j / w;
			y = j % w;
			if (z.substring(i, i + 1).equals("1"))
				board[y][x] = true;
			else
				board[y][x] = false;
			j++;
		}
	}

	/**
	 * @param b
	 *            bestehendes Spielbrett (kann zum Dublizieren von
	 *            Board-Objekten benutzt werden)
	 */
	public Board(Board b) {
		w = b.getWidth();
		h = b.getHeight();
		board = new boolean[w][h];
		for (int i = 1; i <= w; i++) {
			for (int j = 1; j <= h; j++) {
				board[i - 1][j - 1] = b.isSet(i, j);
			}
		}
	}

	/**
	 * Kopiert die Belegungen eines Spielbretts an eine bestimmte Stelle.
	 * 
	 * @param b
	 *            Spielbrett, von dem eingefügt werden soll
	 * @param x
	 *            x-Koordinate des Punkts, an dem eingefügt werden soll
	 * @param y
	 *            y-Koordinate des Einfügepunkts
	 * @param row
	 *            true, wenn in Zeile; false, wenn in Spalte eingefügt werden
	 *            soll
	 */
	public void pasteFrom(Board b, int x, int y, boolean row) {
		for (int i = 1; i <= b.getWidth(); i++) {
			if (row) {
				if (b.isSet(i, 1))
					set(x + i - 1, y);
			} else {
				if (b.isSet(i, 1))
					set(x, y + i - 1);
			}
		}
	}

	/**
	 * @return Liste (HashSet) aller Löcher des Spielbretts
	 */
	public HashSet getHoles() {
		HashSet v = getHolesRow();
		v.addAll(getHolesColumn());
		return v;
	}

	/**
	 * @return alle Löcher für Zeilen
	 */
	public HashSet getHolesRow() {
		HashSet holes = new HashSet();
		for (int row = 1; row <= h; row++) {
			int i = 1;
			int start = 1;
			int length = 0;
			while (i <= w) {
				length = 0;
				while (isFree(i, row)) {
					length++;
					i++;
				}
				if (length > 1)
					holes.add(new Hole(start, row, length, true));
				i++;
				start = i;
			}
		}
		return holes;
	}

	/**
	 * @return alle Löcher für Spalten
	 */
	public HashSet getHolesColumn() {
		HashSet holes = new HashSet();
		for (int column = 1; column <= w; column++) {
			int i = 1;
			int start = 1;
			int length = 0;
			while (i <= h) {
				length = 0;
				while (isFree(column, i)) {
					length++;
					i++;
				}
				if (length > 1)
					holes.add(new Hole(column, start, length, false));
				i++;
				start = i;
			}
		}
		return holes;
	}

	/**
	 * Gibt das Spielfeld auf der Konsole aus. X ... Feld belegt - ... Feld frei
	 */
	public void output() {
		System.out.println(toString());
	}

	/**
	 * Gibt das Spielfeld in eine Datei aus aus. X ... Feld belegt - ... Feld
	 * frei
     *
	 * @param filename Dateiname
	 */
	public void outputFile(String filename) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filename, true)));
		} catch (FileNotFoundException e) {
			System.out.println(e);
		}
		try {
			for (int i = 0; i < board[0].length; i++) {
				for (int j = 0; j < board.length; j++) {
					if (board[j][i] == true)
						out.write("X ");
					else
						out.write("- ");

				}
				out.newLine();
			}
			out.newLine();
			out.close();
		} catch (IOException e1) {
			System.out.println(e1);
		}
	}

	/**
	 * Gibt eine Binärdarstellung des Spielbretts (als Dezimalzahl) zurück.
	 * 
	 * z.B.: 1 1 1 0 0 0 0 0 1 --> 1*2^0 + 1*2^1 + 1*2^2 + 1*2^8 = 263.
	 * 
	 * @return Binärkodierung des Spielbretts
	 */
	public long flatten() {
		int length = w * h;
		long value = 0;
		int x, y = 0;
		for (int i = 0; i < length; i++) {
			x = i / w;
			y = i % w;
			if (board[y][x])
				value += Math.pow(2, i);
		}
		return value;
	}

	/**
	 * @param x
	 *            horizontale Koordinate
	 * @param y
	 *            vertikale Koordinate
	 * @return true, wenn Feld(x,y) belegt ist false, wenn frei
	 */
	public boolean isSet(int x, int y) {
		boolean ret = false;
		try {
			ret = board[x - 1][y - 1];
		} catch (ArrayIndexOutOfBoundsException e) {
			return true;
		}
		return ret;
	}

	/**
	 * @param x
	 *            horizontale Koordinate
	 * @param y
	 *            vertikale Koordinate
	 * @return true, wenn Feld(x,y) frei ist false, wenn belegt
	 */
	public boolean isFree(int x, int y) {
		boolean ret = false;
		try {
			ret = !board[x - 1][y - 1];
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		return ret;
	}

	/**
	 * Setzt das Feld an der Position (x,y) des Spielbretts auf true (belegt).
	 * 
	 * @param x
	 *            horizontale Koordinate
	 * @param y
	 *            vertikale Koordinate
	 */
	public void set(int x, int y) {
		board[x - 1][y - 1] = true;
	}

	/**
	 * Setzt das Feld an der Position (x,y) des Spielbretts auf false (nicht
	 * belegt).
	 * 
	 * @param x
	 *            horizontale Koordinate
	 * @param y
	 *            vertikale Koordinate
	 */
	public void clear(int x, int y) {
		board[x - 1][y - 1] = false;
	}

	/**
	 * Setzt das Feld an der Position (x,y) des Spielbretts auf true (belegt).
	 * 
	 * @param x
	 *            horizontale Koordinate
	 * @param y
	 *            vertikale Koordinate
	 */
	public void set2(int x, int y) {
		board[x - 1][y - 1] = true;
		makeNormalForm();
	}

	/**
	 * Setzt das Feld an der Position (x,y) des Spielbretts auf false (nicht
	 * belegt).
	 * 
	 * @param x
	 *            horizontale Koordinate
	 * @param y
	 *            vertikale Koordinate
	 */
	public void clear2(int x, int y) {
		board[x - 1][y - 1] = false;
		makeNormalForm();
	}

	/**
	 * @return Breite des Spielfelds
	 */
	public int getWidth() {
		return w;
	}

	/**
	 * @return Höhe des Spielfelds
	 */
	public int getHeight() {
		return h;
	}

	/**
	 * @return true, wenn das Spielfeld leer ist
	 */
	public boolean isEmpty() {
		for (int i = 1; i <= w; i++) {
			for (int j = 1; j <= h; j++) {
				if (isSet(i, j))
					return false;
			}
		}
		return true;
	}

	/**
	 * Gibt die Binärdarstellung einer Dezimalzahl zurück.
     *
	 * @param zahl Zahl
	 * @return Binärdarstellung der Zahl
	 */
	private String decimalToBinary(long zahl) {
		String z = "";
		long r;
		do {
			r = zahl % 2;
			z = r + z;
			zahl = zahl / 2;
		} while
			(zahl != 0);
		return z;
	}

	/**
	 * Füllt das 2-elementige Array, welches das Spielbrett
	 * darstellt mit false (nicht belegt) auf.
	 */
	public void fill() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = false;
			}
		}
	}

	/**
	 * Erzeugt eine Darstellung des Spielbretts als bipartiter Graph. Dazu wird
	 * eine Schachbrettfärbung verwendet. Alle Felder, für die gilt Zeilenindex +
	 * Spaltenindex = gerade, werden als weiß, alle anderen als schwarz
	 * angesehen. Alle freien Felder des Spielfelds entsprechen einer Ecke.
	 * Kanten gehen von jeder weißen Ecke zu allen benachbarten (maximal 4)
	 * schwarzen Ecken. Die Anzahl der maximal möglichen Domino-Züge kann nun
	 * als maximum matching dieses (bipartiten) Graphen berechnet werden.
	 * 
	 * @return SimpleGraph (für Berechnung des maximum Matching) der
	 *         Spielposition (bipartit)
	 */
	public SimpleGraph getSimpleGraph() {
		String name;
		String succName;
		SimpleGraph g = new SimpleGraph();
		for (int i = 1; i <= w; i++) {
			for (int j = 1; j <= h; j++) {
				if ((i + j) % 2 == 0) {
					if (isFree(i, j)) {
						name = ((Integer) (i)).toString()
								+ ((Integer) (j)).toString();
						Vertex v = new Vertex(name, true);
						g.addVertex(v);
						if (i - 1 > 0) {
							succName = ((Integer) (i - 1)).toString()
									+ ((Integer) (j)).toString();
							Vertex succ = new Vertex(succName, false);
							g.addVertex(succ);
							if (isFree(i - 1, j)) {
								v.addSucc(succ);
							}
						}
						if (i + 1 <= w) {
							succName = ((Integer) (i + 1)).toString()
									+ ((Integer) (j)).toString();
							Vertex succ = new Vertex(succName, false);
							g.addVertex(succ);
							if (isFree(i + 1, j)) {
								v.addSucc(succ);
							}
						}
						if (j - 1 > 0) {
							succName = ((Integer) (i)).toString()
									+ ((Integer) (j - 1)).toString();
							Vertex succ = new Vertex(succName, false);
							g.addVertex(succ);
							if (isFree(i, j - 1)) {
								v.addSucc(succ);
							}
						}
						if (j + 1 <= h) {
							succName = ((Integer) (i)).toString()
									+ ((Integer) (j + 1)).toString();
							Vertex succ = new Vertex(succName, false);
							g.addVertex(succ);
							if (isFree(i, j + 1)) {
								v.addSucc(succ);
							}
						}
					} else {
						name = ((Integer) (i)).toString()
								+ ((Integer) (j)).toString();
						Vertex v = new Vertex(name, true);
						g.addVertex(v);
					}
				}
			}
		}
		return g;
	}

	/**
	 * Bringt das Spielfeld in eine Normalform (nur für "einfaches" Juvavum).
	 */
	public void makeNormalForm() {
		int z[], s[], bz[], bs[];
		z = new int[h];
		s = new int[w];
		bz = new int[h];
		bs = new int[w];
		for (int i = 1; i <= h; i++) {
			z[i - 1] = 0;
			bz[i - 1] = 0;
			for (int j = 1; j <= w; j++) {
				if (isFree(j, i))
					z[i - 1]++;
				else
					bz[i - 1] += Math.pow(2, j - 1);

			}
		}
		for (int j = 1; j <= w; j++) {
			s[j - 1] = 0;
			bs[j - 1] = 0;
			for (int i = 1; i <= h; i++) {
				if (isFree(j, i))
					s[j - 1]++;
				else
					bs[j - 1] += Math.pow(2, i - 1);

			}
		}
		sortZeilen(z, bz);
		sortSpalten(s, bs);
	}

	/**
	 * sortiert die Zeilen nach Anzahl der belegten Felder
	 */
	private void sortZeilen(int[] z, int[] bz) {
		int tausch = 0;
		boolean help = false;
		int n = z.length;
		for (int i = 0; i < n - 1; i++) {
			for (int j = n - 1; j > i; j--) {
				if (z[j - 1] > z[j]) {
					// vertausche
					tausch = z[j - 1];
					z[j - 1] = z[j];
					z[j] = tausch;
					for (int k = 0; k < w; k++) {
						help = board[k][j - 1];
						board[k][j - 1] = board[k][j];
						board[k][j] = help;
					}
				}
			}
		}
	}

	/**
	 * sortiert die Spalten nach Anzahl der belegten Felder
	 */
	private void sortSpalten(int[] s, int[] bs) {
		int tausch = 0;
		boolean help = false;
		int n = s.length;
		for (int i = 0; i < n - 1; i++) {
			for (int j = n - 1; j > i; j--) {
				if (s[j - 1] > s[j]) {
					// vertausche
					tausch = s[j - 1];
					s[j - 1] = s[j];
					s[j] = tausch;
					for (int k = 0; k < h; k++) {
						help = board[j - 1][k];
						board[j - 1][k] = board[j][k];
						board[j][k] = help;
					}
				}
			}
		}
	}

	/**
	 * Durchsucht das Spielfeld nach isolierten Feldern (Felder, die auf allen
	 * Seiten von einem belgten Feld oder dem Spielfeldrand umgeben sind) und
	 * gibt deren Anzahl zurück.
	 * 
	 * @return Anzahl der isolierten Felder
	 */
	public int getNumIsolated() {
		int num = 0;
		for (int i = 1; i <= w; i++)
			for (int j = 1; j <= h; j++)
				if (isFree(i, j) && isSet(i - 1, j) && isSet(i + 1, j)
						&& isSet(i, j - 1) && isSet(i, j + 1))
					num++;
		return num;
	}

	/**
	 * @return Anzahl der freien Felder
	 */
	public int getNumFree() {
		int num = 0;
		for (int i = 1; i <= w; i++)
			for (int j = 1; j <= h; j++)
				if (isFree(i, j))
					num++;
		return num;
	}

	/**
	 * Spiegelt das Spielbrett vertikal
	 */
	public Board fliplr() {
		int dy = board.length;
		int dx = board[0].length;
		boolean help;
		for (int xi = 0; xi < dx; xi++) {
			for (int yi = 0; yi < dy / 2; yi++) {
				help = board[yi][xi];

				board[yi][xi] = board[dy - 1 - yi][xi];
				board[dy - 1 - yi][xi] = help;
			}
		}
		return this;
	}

	/**
	 * Spiegelt das Spielbrett horizontal
	 */
	public Board flipud() {
		int dy = board.length;
		int dx = board[0].length;
		boolean help;
		for (int xi = 0; xi < dx / 2; xi++) {
			for (int yi = 0; yi < dy; yi++) {
				help = board[yi][xi];
				board[yi][xi] = board[yi][dx - 1 - xi];
				board[yi][dx - 1 - xi] = help;
			}
		}
		return this;
	}

	/**
	 * Dreht das Spielbrett um 180 Grad
	 */
	public Board rotate180() {
		flipud();
		fliplr();
		return this;
	}

	/**
	 * Dreht das Spielbrett um 270 Grad
	 */
	public Board rotate270() {
		boolean[][] help = new boolean[w][h];
		for (int i = 1; i <= h; i++) {
			for (int j = 1; j <= w; j++) {
				help[j - 1][w - i] = isSet(i, j);
			}
		}
		board = help;
		return this;
	}

	/**
	 * Dreht das Spielbrett um 90 Grad (im Uhrzeigersinn)
	 */
	public Board rotate90() {
		boolean[][] help = new boolean[w][h];
		for (int i = 1; i <= h; i++) {
			for (int j = 1; j <= w; j++) {
				help[h - j][i - 1] = isSet(i, j);
			}
		}
		board = help;
		return this;
	}

	/**
	 * Spielfeld an der Hauptdiagonale spiegeln
	 */
	public Board flipd1() {
		boolean help;
		for (int i = 1; i < board.length; i++) {
			for (int j = 0; j < i; j++) {
				help = board[i][j];
				board[i][j] = board[j][i];
				board[j][i] = help;
			}
		}
		return this;
	}

	/**
	 * Spielfeld an der Nebendiagonale spiegeln
	 */
	public Board flipd2() {
		boolean help;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < w - 1 - i; j++) {
				help = board[i][j];
				board[i][j] = board[h - 1 - j][w - 1 - i];
				board[h - 1 - j][w - 1 - i] = help;
			}
		}
		return this;
	}

	/**
	 * Verwandelt ein m x n-Spielbrett durch Drehung
	 * um 90 Grad in ein n x m - Spielbrett
	 */
	public Board turn90() {
		Board turn = new Board(w, h);
		for (int i = 1; i <= w; i++)
			for (int j = 1; j <= h; j++)
				if (isSet(i, j))
					turn.set(j, i);
		return turn;
	}

	/**
	 * String-Repräsentierung des Spielbretts
	 */
	public String toString() {
		String ret = "";
		for (int i = 0; i < board[0].length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (board[j][i] == true)
					ret += ("X ");
				else
					ret += ("- ");

			}
			ret += "\n";
		}
		return ret;
	}
}