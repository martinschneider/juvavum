package juvavum.game.players;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import juvavum.analyse.Board;
import juvavum.analyse.GameAnalyse;
import juvavum.util.List;

/**
 * Allround Player
 * 
 * Dieser Spieler greift auf die Funktionen zweier anderer zuruck. In den Fallen, in denen der
 * Better Symmetric Player garantiert gewinnen wird (1. Spieler auf gerade x ungerade und
 * ungerade x gerade-Feldern bzw. 2. Spieler im Fall gerade x gerade jeweils im normalen
 * Spiel) befragt er diesen Spieler, andernfalls den Perfect Endgame Player. Auf Spielfeldern,
 * füur die man noch keine Grundywerttabellen berechnet hat, stellt dieser Spieler die beste
 * Wahl dar und ist ohne das Wissen uber eine Gewinnstrategie von einem menschlichen
 * Spieler bereits sehr schwer zu schlagen.
 * 
 * @author Martin Schneider
 */
public class AllroundPlayer implements Player {

	public List pos = new List();
	private Board b;
	private int gameType;
	private boolean misere;
	private boolean humanStarts;
	private GameAnalyse analyse;

	/**
	 * @param gameType
	 * 					1 - Juvavum, 2 - Domino Juvavum, 3 - Cram, 4 - Hole Cram, 5- Triomino Juvavum
	 * @param misere
	 * 					true, wenn Misere-Form
	 * @param humanStarts
	 * 					true, wenn der menschliche Spieler beginnt
	 * @param analyse
	 * 					Analyse des Spiels
	 */
	public AllroundPlayer(int gameType, boolean misere, boolean humanStarts, GameAnalyse analyse) {
		this.gameType=gameType;
		this.misere=misere;
		this.humanStarts=humanStarts;
		this.analyse=analyse;
	}

	/* (non-Javadoc)
	 * @see juvavum.game.players.Player#getName()
	 */
	public String getName() {
		return "Allround";
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
		Player p;
		int h=b.getHeight();
		int w=b.getWidth();
		
		// wenn Grundytabelle für Spielfeld existiert -> verwenden
		boolean turn=false;
		if (h>w){
			b=b.turn90();
			int help;
			help=h;
			h=w;
			w=help;
			turn=true;
		}
		String game="";
		if (gameType==1) game="JUV";
		else if (gameType==2) game="DJUV";
		else if (gameType==3) game="CRAM";
		else if (gameType==4) game="HOLE";
		else if (gameType==5) game="TJUV";
		if (misere){
			if (game=="JUV") game="JUVM";
			else if (game=="DJUV") game="DJUVM";
			else if (game=="CRAM") game="MCRAM";
			else if (game=="HOLE") game="HOLEM";
			else if (game=="TJUV") game="TJUVM";
		}
		String file = "/juvavum/grundy/"+game+"["+h+"x"+w+"].txt";
        try {
        	InputStream is = this.getClass().getResourceAsStream(file);
            new BufferedReader(new InputStreamReader(is));
			p=new PerfectPlayer(gameType,misere,analyse);
			p.setBoard(b);
			return p.getSucc();
		} catch (Exception e) {
		}

		if (turn){
			b=b.turn90();
			int help;
			help=w;
			w=h;
			h=help;
		}
		
		// wenn gerade*gerade und normales Spiel und 2. Spieler -> BetterSymmetricPlayer
		if (h%2==0 && w%2==0 && !misere && humanStarts){
			p=new BetterSymmetricPlayer(gameType);
			p.setBoard(b);
			return p.getSucc();
		}
		
		// wenn gerade*ungerade und normales Spiel und 1. Spieler -> BetterSymmetricPlayer
		if (((h%2==0 && w%2==1)||(h%2==1 && w%2==0)) && !misere && !humanStarts){
			p=new BetterSymmetricPlayer(gameType);
			p.setBoard(b);
			return p.getSucc();
		}
		
		// sonst -> PerfectEndgamePlayer
		p=new PerfectEndgamePlayer(gameType,misere);
		p.setBoard(b);
		return p.getSucc();
	}
}