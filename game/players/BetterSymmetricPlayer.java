package juvavum.game.players;

import java.util.Enumeration;

import juvavum.analyse.Board;
import juvavum.analyse.CramGameAnalyse;
import juvavum.analyse.DJGameAnalyse;
import juvavum.analyse.HoleGameAnalyse;
import juvavum.analyse.JGameAnalyse;
import juvavum.analyse.TJGameAnalyse;
import juvavum.analyse.GameAnalyse;
import juvavum.analyse.Position;
import juvavum.util.List;
import java.util.Vector;

/**
 * Better Symmetric Player
 * 
 * Dieser Spieler stellt eine Weiterentwicklung des Symmetric Players dar. Insbesondere gewinnt
 * er als 1. Spieler auf allen 2m x (2n + 1) bzw. (2m + 1) x 2n-Feldern. Dieser Spieler steht
 * für Misere-Formen nicht zur Verfugung.
 * 
 * @author Martin Schneider
 */

public class BetterSymmetricPlayer implements Player {

	public List pos = new List();
	private Board b;
	private int gameType;
	private GameAnalyse analyse;

	/**
	 * @param gameType
	 * 					1 - Juvavum, 2 - Domino Juvavum, 3 - Cram, 4 - Hole Cram, 5- Triomino Juvavum
	 */
	public BetterSymmetricPlayer(int gameType) {
		this.gameType=gameType;
	}

	/* (non-Javadoc)
	 * @see juvavum.game.players.Player#getName()
	 */
	public String getName() {
		return "BetterSymmetric";
	}

	/* (non-Javadoc)
	 * @see juvavum.game.players.Player#setBoard(juvavum.analyse.Board)
	 */
	public void setBoard(Board b) {
		this.b = b;
	}

	/* (non-Javadoc)
	 * @see juvavum.game.players.Player#getSucc()
	 */
	public Board getSucc() {
		int w = b.getWidth();
		int h = b.getHeight();
		if (gameType == 1) {
			if (b.isFree(w/2+1, h/2+1)){
				b.set(w/2+1, h/2+1);
				return b;
			}
		}
		if (gameType == 2) {
			if (w % 2 == 0 && h % 2 == 1 & b.isEmpty()) {
				for (int i = 1; i <= w; i++)
					b.set(i, (h / 2) + 1);
				return b;
			}
			if (w % 2 == 1 && h % 2 == 0 & b.isEmpty()) {
				for (int j = 1; j <= h; j++)
					b.set((w / 2) + 1, j);
				return b;
			}
			if (w % 2 == 1 && h % 2 == 1 & b.isEmpty()) {
				b.set(w / 2 + 1, h / 2 + 1);
				double rnd = Math.random();
				if (rnd < 0.25) {
					b.set(w / 2, h / 2 + 1);
					return b;
				}
				if (rnd < 0.5) {
					b.set(w / 2 + 2, h / 2 + 1);
					return b;
				}
				if (rnd < 0.75) {
					b.set(w / 2 + 1, h / 2);
					return b;
				}
				b.set(w / 2 + 1, h / 2 + 2);
				return b;
			}
		}
		Vector<Position> possibleSucc = new Vector<Position>();
		if (gameType == 1)
			analyse = new JGameAnalyse(b);
		else if (gameType == 2)
			analyse = new DJGameAnalyse(b);
		else if (gameType == 3)
			analyse = new CramGameAnalyse(b);
		else if (gameType == 4)
			analyse = new HoleGameAnalyse(b);
		else if (gameType == 5)
			analyse = new TJGameAnalyse(b);
		pos = analyse.getSucc(false);
		Position succ = null;
		Position tmp;
		int currValue = 0;
		int tmpValue = 0;
		if (!pos.get(0).getChildren().isEmpty()) {
			for (Enumeration e1 = pos.get(0).getChildren().elements(); e1
					.hasMoreElements();) {
				tmp = (Position) e1.nextElement();
				tmpValue = getValue(tmp);
				if (tmpValue > currValue) {
					possibleSucc.clear();
					currValue = tmpValue;
					succ = tmp;
				}
				if (tmpValue == currValue) {
					possibleSucc.add(tmp);
				}
			}
		} else
			return null;
		if (!possibleSucc.isEmpty()) {
			int random = (int) (Math.random() * possibleSucc.size());
			succ = (Position) (possibleSucc.get(random));
		}
		return succ.getBoard();
	}

	/**
	 * @param pos
	 * 				Position
	 * @return
	 * 				Binärrepräsentation
	 */
	public int getValue(Position pos) {
		Board b = pos.getBoard();
		int w = b.getWidth();
		int h = b.getHeight();
		int symm = 0;
		for (int i = 1; i <= w; i++) {
			for (int j = 1; j <= h; j++) {
				if (b.isSet(i, j) == b.isSet(w + 1 - i, h + 1 - j))
					symm++;
			}
		}
		return symm;
	}
}