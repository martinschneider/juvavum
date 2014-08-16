package juvavum.game.players;

import java.util.Enumeration;

import juvavum.analyse.Board;
import juvavum.analyse.CramGameAnalyse;
import juvavum.analyse.DJGameAnalyse;
import juvavum.analyse.JGameAnalyse;
import juvavum.analyse.TJGameAnalyse;
import juvavum.analyse.HoleGameAnalyse;
import juvavum.analyse.GameAnalyse;
import juvavum.analyse.Position;
import juvavum.util.List;

/**
 * Perfect Endgame Player
 * 
 * Der Perfect Endgame Player berechnet einige Zuge vor Ende des Spiels (genauer: sobald
 * noch eine gewisse Anzahl an Feldern frei ist) den kompletten Spielegraphen und kann ab
 * diesem Zeitpunkt optimal spielen. Das bedeutet: wenn es eine Gewinnmoglichkeit gibt,
 * wird er sie nutzen. Bis zu diesem Zeitpunkt setzt er wie der Random Player zufallig.
 * 
 * @author Martin Schneider
 */
public class PerfectEndgamePlayer implements Player {

	public List pos = new List();

	private boolean misere;

	private int gameType;

	private GameAnalyse analyse;

	private Board b;

	/**
	 * @param gameType
	 * 					1 - Juvavum, 2 - Domino Juvavum, 3 - Cram, 4 - Hole Cram, 5- Triomino Juvavum
	 * @param misere
	 * 					true, wenn Misere-Form
	 */
	public PerfectEndgamePlayer(int gameType, boolean misere) {
		this.gameType = gameType;
		this.misere = misere;
	}

	/* (non-Javadoc)
	 * @see juvavum.game.players.Player#getName()
	 */
	public String getName() {
		return "PerfectEndgame";
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
		else if (gameType == 2)
			analyse = new DJGameAnalyse(b);
		else if (gameType == 3)
			analyse = new CramGameAnalyse(b);
		else if (gameType == 4)
			analyse = new HoleGameAnalyse(b);
		else if (gameType == 5)
			analyse = new TJGameAnalyse(b);
		int maxZuege=b.getNumFree();
		if (gameType>1) maxZuege=(b.getNumFree()-b.getNumIsolated())/2;
		if (maxZuege > 7) {
			pos = analyse.getSucc(false);
			if (!pos.get(0).getChildren().isEmpty()) {
				int random = (int) (Math.random() * pos.get(0).getChildren()
						.size());
				return ((Position) (pos.get(0).getChildren().get(random)))
						.getBoard();
			}
			return null;
		} else {
			pos = analyse.getSucc(true);
			int foundPos = pos.search(b.flatten());
			for (Enumeration e1 = pos.get(foundPos).getChildren().elements(); e1
					.hasMoreElements();) {
				Position p = (Position) e1.nextElement();
				if (!misere) {
					if (p.getGrundy(2) == 0)
						return p.getBoard();
				} else {
					if (p.getGrundyMisere(2) == 0){
						return p.getBoard();
					}
				}
			}
			if (!pos.get(0).getChildren().isEmpty()) {
				int random = (int) (Math.random() * pos.get(0).getChildren()
						.size());
				return ((Position) (pos.get(0).getChildren().get(random)))
						.getBoard();
			}
		}

		return null;
	}
}