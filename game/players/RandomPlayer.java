package juvavum.game.players;

import juvavum.analyse.Board;
import juvavum.analyse.GameAnalyse;
import juvavum.analyse.DJGameAnalyse;
import juvavum.analyse.JGameAnalyse;
import juvavum.analyse.CramGameAnalyse;
import juvavum.analyse.HoleGameAnalyse;
import juvavum.analyse.TJGameAnalyse;
import juvavum.analyse.Position;
import juvavum.util.List;

/**
 * Random Player
 * 
 * Dieser Spieler zieht stets auf eine beliebige Nachfolgeposition.
 * 
 * @author Martin Schneider
 */
public class RandomPlayer implements Player {

	public List pos = new List();
	private GameAnalyse analyse;
	private int gameType;
	Board b;

	/**
	 * @param gameType
	 *            1 - Juvavum, 2 - Domino Juvavum, 3 - Cram, 4 - Hole Cram, 5-
	 *            Triomino Juvavum
	 */
	public RandomPlayer(int gameType) {
		this.gameType = gameType;
	}

	/* (non-Javadoc)
	 * @see juvavum.game.players.Player#setBoard(juvavum.analyse.Board)
	 */
	public void setBoard(Board b) {
		this.b = b;
	}

	/* (non-Javadoc)
	 * @see juvavum.game.players.Player#getName()
	 */
	public String getName() {
		return "Random";
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
		Position succ;
		if (!pos.get(0).getChildren().isEmpty()) {
			int random = (int) (Math.random() * pos.get(0).getChildren().size());
			succ = (Position) (pos.get(0).getChildren().get(random));
		} else
			return null;
		return succ.getBoard();
	}
}