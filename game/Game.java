package juvavum.game;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import juvavum.analyse.*;
import juvavum.game.players.Player;
import juvavum.gui.BoardGUI;
import juvavum.gui.GameOptionsGUI;

/**
 * Klasse um zwei Spieler gegeneinander antreten zu lassen
 * 
 * @author Martin Schneider
 */
public class Game extends BoardGUI {

	private Board b;
	
	private GameBoard gb;

	private int w;

	private int h;

	int historyCount = 0;

	private GameAnalyse analyse;

	private Player opponent;

	private String playerName;

	private boolean gameOver = false;
	
	private boolean misere = false;
	
	private GameOptionsGUI caller;
	
	private int playerNumber;

	/**
	 * @param b
	 * 			Spielbrett
	 * @param opponent
	 * 			Computergegner
	 * @param gameType
	 * 			1 - Juvavum, 2 - Domino Juvavum, 3 - Cram, 4 - Hole Cram, 5- Triomino Juvavum
	 * @param analyse
	 * 			Analyse des Spiels
	 * @param humanStarts
	 * 			true, wenn der menschliche Spieler den ersten Zug macht
	 * @param playerName
	 * 			Spielername
	 * @param misere
	 * 			true, wenn in der Misere-Form gespielt wird
	 * @param caller
	 * 			Verweis auf das GUI, das das Spiel aufruft
	 */
	public Game(Board b, Player opponent, int gameType, GameAnalyse analyse,
			boolean humanStarts, String playerName, boolean misere, GameOptionsGUI caller) {
		this.w = b.getWidth();
		this.h = b.getHeight();
		this.b = b;
		this.playerName=playerName;
		gb=new GameBoard(b);
		this.misere=misere;
		this.caller = caller;
		this.analyse = analyse;
		playerNumber=2;
		if (humanStarts) playerNumber=1;
		opponent.setBoard(b);
		this.opponent = opponent;
		setSize(w * 50 + 20, h * 50 + 40);
		addMouseListener(ml);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}
		});
		// setVisible(true);
		if (!humanStarts) {
			Board succ = opponent.getSucc();
			if (succ != null) {
				makeGameBoard(succ,gb);
				b = succ;
				analyse.setBoard(b);
				if (analyse.getFirstSucc() == null) {
					gameOver = true;
					repaint();
					try {
						Thread.currentThread().sleep(500);
					} catch (InterruptedException e1) {
					}
					if (!misere) showWinner("Computer");
					else showWinner(playerName);
				}
			} else {
				gameOver = true;
				try {
					Thread.currentThread().sleep(500);
				} catch (InterruptedException e1) {
				}
				if (!misere) showWinner(playerName);
				else showWinner("Computer");
			}
		}
	}

	/* (non-Javadoc)
	 * @see juvavum.gui.BoardGUI#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		int tmp;
		for (int i = 1; i <= w; i++) {
			for (int j = 1; j <= h; j++) {
				tmp=0;
				if (gb.valueAt(i,j)==1) tmp=1;
				else if (gb.valueAt(i,j)==2) tmp=2;
				else if (gb.valueAt(i,j)==3) tmp=playerNumber;
				else if (gb.valueAt(i,j)==-1) tmp=-1;
				if (tmp==1) {
					g.setColor(Color.red);
					g.fillRect((i - 1) * 50 + 10, (j - 1) * 50 + 30, 50, 50);
				}
				else if (tmp==2) {
					g.setColor(Color.blue);
					g.fillRect((i - 1) * 50 + 10, (j - 1) * 50 + 30, 50, 50);
				}
				else if (tmp==0){
					g.setColor(Color.white);
					g.fillRect((i - 1) * 50 + 10, (j - 1) * 50 + 30, 50, 50);
				}
				else if (tmp==-1){
					g.setColor(Color.black);
					g.fillRect((i - 1) * 50 + 10, (j - 1) * 50 + 30, 50, 50);
				}
				g.setColor(Color.black);
				g.drawRect((i - 1) * 50 + 10, (j - 1) * 50 + 30, 50, 50);
			}
		}
	}

	/**
	 * Spielzug durchführen (Spielerzug überprüfen, Zug des Computers abfragen)
	 */
	public void move() {
		GameBoard gbTmp=new GameBoard(gb);
		for (int i = 1; i <= w; i++) {
			for (int j = 1; j <= h; j++) {
				if (gb.valueAt(i,j)==3) gbTmp.set(i, j, playerNumber);
			}
		}
		b=gb.getBoard();
		if (!(analyse.isValidSucc(b))) {
			showError("Ungültiger Zug.");
			repaint();
		} else {
			gb=new GameBoard(gbTmp);
			opponent.setBoard(b);
			Board succ = opponent.getSucc();
			if (succ != null) {
				makeGameBoard(succ,gb);
				b = succ;
				analyse.setBoard(b);
				if (analyse.getFirstSucc() == null) {
					gameOver = true;
					repaint();
					try {
						Thread.currentThread().sleep(500);
					} catch (InterruptedException e1) {
					}
					if (!misere) showWinner("Computer");
					else showWinner(playerName);
				}
			} else {
				gameOver = true;
				try {
					Thread.currentThread().sleep(500);
				} catch (InterruptedException e1) {
				}
				if (!misere) showWinner(playerName);
				else showWinner("Computer");
			}
		}
		if (gameOver) {
			dispose();
		}
		repaint();
	}
	
	/**
	 * Erstellt das GameBoard der Spielposition. Im Gegensatz zur Klasse Board
	 * speichert GameBoard die Information von welchem Spieler ein Feld belegt
	 * wurde. 
	 * 
	 * @param b
	 * 		Board der aktuellen Spielposition (enthält keine Informationen, wer welches Feld belegt hat)
	 * @param curr
	 * 		GameBoard vor dem jetzigen Zug (enthält Informationen, wer welches Feld besetzt hat)
	 */
	public void makeGameBoard(Board b, GameBoard curr){
		int computer=1;
		if (playerNumber==1) computer=2;
		for (int i = 1; i <= w; i++) {
			for (int j = 1; j <= h; j++) {
				if (curr.valueAt(i, j)==0 && b.isSet(i, j))
					this.gb.set(i, j, computer);
			}
		}
	}

	/**
	 * MouseListener für das Spielfeld
	 */
	private MouseListener ml = new /**
	 * @author L & M
	 *
	 */
	MouseListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent e) {
			if (e.isAltDown())
				dispose();
			else if (e.isShiftDown())
				move();
			else {
				int x = (e.getX() - 10) / 50;
				int y = (e.getY() - 30) / 50;
				if (gb.valueAt(x + 1, y + 1)==3)
					gb.clear(x + 1, y + 1);
				else if (gb.valueAt(x + 1, y + 1)==0)
					gb.set(x + 1, y + 1,3);
			}
			repaint();
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent e) {
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		public void mouseEntered(MouseEvent e) {
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		public void mouseExited(MouseEvent e) {
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent e) {
		}
	};

	/**
	 * @param winner
	 * 				Gewinner
	 */
	public void showWinner(String winner) {
		caller.declareWinner(winner,true);
	}

	/**
	 * @param error
	 * 				Fehlermeldung
	 */
	public void showError(String error) {
		caller.showError(error);
	}
	
	/**
	 * @return
	 * 				true, wenn Spiel zu Ende
	 */
	public boolean getGameOver(){
		return gameOver;
	}
}
