package juvavum.game.players;

import java.util.Enumeration;

import juvavum.analyse.Board;
import juvavum.analyse.CramGameAnalyse;
import juvavum.analyse.DJGameAnalyse;
import juvavum.analyse.JGameAnalyse;
import juvavum.analyse.GameAnalyse;
import juvavum.analyse.TJGameAnalyse;
import juvavum.analyse.HoleGameAnalyse;
import juvavum.analyse.Position;
import juvavum.util.List;

/**
 * Symmetric Player
 * 
 * Dieser Spieler versucht mit Hilfe einer Bewertungsfunktion stets auf eine
 * moglichst symmetrische Spielposition zu ziehen. Dadurch wird er als 2.
 * Spieler auf 2m x 2n-Feldern stest gewinnen. Auch in allen ubrigen Fallen
 * stellt er (zumindest anfangs) einen erstnzunehmenden Gegner dar. Da diese
 * symmetrsiche Spielweise fur Misere-Juvavum keinen Sinn macht, steht der
 * Symmetric Player fur diese Spiele nicht zur Verfugung.
 * 
 * @author Martin Schneider
 */
public class SymmetricPlayer implements Player {

	public List pos = new List();
	private GameAnalyse analyse;
	private int gameType;
	private Board b;

	/**
 	 * @param gameType
	 * 					1 - Juvavum, 2 - Domino Juvavum, 3 - Cram, 4 - Hole Cram, 5- Triomino Juvavum
	 */
	public SymmetricPlayer(int gameType) {
		this.gameType = gameType;
	}

	/* (non-Javadoc)
	 * @see juvavum.game.players.Player#getName()
	 */
	public String getName() {
		return "Symmetric";
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
		if (gameType == 1)
			analyse = new JGameAnalyse(b);
		if (gameType == 2)
			analyse = new DJGameAnalyse(b);
		if (gameType == 3)
			analyse = new CramGameAnalyse(b);
		if (gameType == 4)
			analyse = new HoleGameAnalyse(b);
		if (gameType == 5)
			analyse = new TJGameAnalyse(b);
		pos = analyse.getSucc(false);
		Position succ = null;
		Position tmp;
		int currValue = -1000;
		int tmpValue = 0;
		if (!pos.get(0).getChildren().isEmpty()) {
			for (Enumeration e1 = pos.get(0).getChildren().elements(); e1
					.hasMoreElements();) {
				tmp = (Position) e1.nextElement();
				tmpValue = getValue(tmp);
				if (tmpValue >= currValue) {
					currValue = tmpValue;
					succ = tmp;
				}
			}
		} else
			return null;
		return succ.getBoard();
	}

	/**
	 * @param pos
	 * 			Spielposition
	 * @return
	 * 			Binärrepräsentation von pos
	 */
	private int getValue(Position pos) {
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