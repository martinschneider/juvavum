package juvavum.game;

import juvavum.analyse.Board;

/**
 * Repräsentation eines Spielfelds für das Spielen von Juvavum.
 * Im Unterschied zur Klasse Board wird hier die Information
 * gespeichert welcher Spieler ein bestimmtes Feld belegt hat.
 * Dadurch können die Spielsteine von A (1. Spieler) in einer
 * anderen Farbe als jene von B (2. Spieler) angezeigt werden. 
 * 
 * @author Martin Schneider
 */
public class GameBoard {
	private int[][] board;

	private int h;

	private int w;
	
	/**
	 * @return
	 * 			Breite
	 */
	public int getWidth(){
		return w;
	}
	
	/**
	 * @return
	 * 			Höhe
	 */
	public int getHeight(){
		return h;
	}

	/**
	 * Erstellt ein leeres Spielfeld-Objekt (alle Felder werden als frei
	 * initialisiert)
	 * 
	 * @param w
	 *            Breite des Spielfelds
	 * @param h
	 *            Höhe des Spielfelds
	 */
	public GameBoard(int h, int w) {
		if (h*w>=63) throw new IllegalArgumentException("Spielfelder mit mehr als 63 Feldern sind in dieser Version noch nicht möglich.");
		this.board = new int[w][h];
		this.h = h;
		this.w = w;
		fill();
	}

	/**
	 * @param b
	 *            bestehendes Spielbrett (kann zum Dublizieren von
	 *            GameBoard-Objekten benutzt werden)
	 */
	public GameBoard(GameBoard b) {
		w = b.getWidth();
		h = b.getHeight();
		board = new int[w][h];
		for (int i = 1; i <= w; i++) {
			for (int j = 1; j <= h; j++) {
				board[i - 1][j - 1] = b.valueAt(i, j);
			}
		}
	}

	/**
	 * @param b
	 * 			bestehendes Spielbrett vom Typ Board
	 */
	public GameBoard(Board b) {
		w = b.getWidth();
		h = b.getHeight();
		board = new int[w][h];
		for (int i = 1; i <= w; i++) {
			for (int j = 1; j <= h; j++) {
				if (b.isSet(i,j)) board[i-1][j-1]=-1;
			}
		}
	}

	
	/**
	 * Gibt das Spielfeld auf der Konsole aus. X ... Feld belegt - ... Feld frei
	 */
	public void output() {
		for (int i = 0; i < board[0].length; i++) {
			for (int j = 0; j < board.length; j++) {
				// System.out.print("("+i+","+j+")");
				System.out.print(board[j][i]+" ");
			}
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * @param x
	 * 			x-Koordinate
	 * @param y
	 * 			y-Koordinate
	 * @return
	 * 			0 - nicht belegt, 1 - von 1. Spieler belegt, 2- von 2. Spieler belegt
	 */
	public int valueAt(int x, int y) {
		int ret = 0;
		try {
			ret = board[x - 1][y - 1];
		} catch (ArrayIndexOutOfBoundsException e) {
			return 0;
		}
		return ret;
	}

	/**
	 * Setzt das Feld an der Position (x,y) des Spielbretts auf belegt (1 - 1. Spieler, 2 - 2. Spieler).
	 * 
	 * @param x
	 *            horizontale Koordinate
	 * @param y
	 *            vertikale Koordinate
	 */
	public void set(int x, int y, int player) {
		board[x - 1][y - 1] = player;
	}

	/**
	 * Setzt das Feld an der Position (x,y) des Spielbretts auf nicht
	 * belegt.
	 * 
	 * @param x
	 *            horizontale Koordinate
	 * @param y
	 *            vertikale Koordinate
	 */
	public void clear(int x, int y) {
		board[x - 1][y - 1] = 0;
	}
	
	/**
	 * Füllt das 2-elementige Array, welches das Spielbrett
	 * darstellt mit false (nicht belegt) auf.
	 */
	public void fill() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = 0;
			}
		}
	}
	
	/**
	 * @return
	 * 			Spielbrett als Board-Objekt
	 */
	public Board getBoard(){
		Board b=new Board(h,w);
		for (int i = 1; i <= w; i++) {
			for (int j = 1; j <= h; j++) {
				if (valueAt(i, j)!=0) b.set(i, j); 
			}
		}
		return b;
	}
	
}