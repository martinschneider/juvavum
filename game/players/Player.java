package juvavum.game.players;

import juvavum.analyse.Board;

/**
 * Interface für die Computerspieler
 * 
 * @author Martin Schneider
 */
public interface Player {
	
	public Board getSucc();
	
	public void setBoard(Board b);
	
	public String getName();
	
}
