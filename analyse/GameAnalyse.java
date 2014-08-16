package juvavum.analyse;

import juvavum.util.List;

/**
 * Interface f�r Analyseprogramm
 * 
 * @author Martin Schneider
 */
public interface GameAnalyse{
	
	public void setBoard(Board b);
	
	public Board getBoard();
	
	public List getSucc(boolean recursive);
	
	public Board getFirstSucc();
	
	public boolean isValidSucc(Board succ);

	public int findSymmetricPosition(Board b);
}