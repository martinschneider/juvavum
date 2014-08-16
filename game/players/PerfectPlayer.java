package juvavum.game.players;

import juvavum.analyse.Board;
import juvavum.analyse.GameAnalyse;
import juvavum.analyse.DJGameAnalyse;
import juvavum.analyse.JGameAnalyse;
import juvavum.analyse.CramGameAnalyse;
import juvavum.analyse.TJGameAnalyse;
import juvavum.analyse.HoleGameAnalyse;
import juvavum.analyse.Position;
import juvavum.util.List;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * Perfect Player
 * 
 * Wie der Name bereits vermuten lasst, spielt dieser Spieler (in gewissem
 * Sinne) perfekt. Mit Hilfe von zuvor berechneten Grundywerttabellen (in Form
 * von Textdateien), kann er immer auf eine Nachfolgeposition mit Grundywert 0
 * ziehen, falls es eine solche gibt. Kurz: Wenn er gewinnen kann, wird er
 * gewinnen. Das bedeutet, dass ein menschlicher Spieler um ihn zu schlagen
 * erstens eine Gewinnstrategie besitzen muss und zudem uber den gesamten
 * Spielverlauf keinen Fehler (d.h. Zug auf eine Position mit Grundywert > 0)
 * machen darf.
 * 
 * @author Martin Schneider
 */

public class PerfectPlayer implements Player {

	public List pos = new List();
	private GameAnalyse analyse;
	private int gameType;
	private int h;
	private int w;
	private boolean misere;
	Board b;
	private Icon errorIcon = new ImageIcon(this.getClass().getResource(
			"/juvavum/images/domino2.gif"));

	/**
	 * @param gameType
	 * 					1 - Juvavum, 2 - Domino Juvavum, 3 - Cram, 4 - Hole Cram, 5- Triomino Juvavum
	 * @param misere
	 * 					true, wenn Misere-Form
	 * @param analyse
	 * 					Analyse des Spiels
	 */
	public PerfectPlayer(int gameType, boolean misere, GameAnalyse analyse) {
		this.gameType = gameType;
		this.misere = misere;
		this.analyse = analyse;
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
		h = b.getHeight();
		w = b.getWidth();
		analyse.setBoard(new Board(b));
		boolean turn = false;
		if (h > w) {
			b = b.turn90();
			int help;
			help = h;
			h = w;
			w = help;
			turn = true;
		}
		long value = b.flatten();
		String game = "";
		if (gameType == 1)
			game = "JUV";
		else if (gameType == 2)
			game = "DJUV";
		else if (gameType == 3)
			game = "CRAM";
		else if (gameType == 4)
			game = "HOLE";
		else if (gameType == 5)
			game = "TJUV";
		if (misere) {
			if (game == "JUV")
				game = "JUVM";
			else if (game == "DJUV")
				game = "DJUVM";
			else if (game == "CRAM")
				game = "MCRAM";
			else if (game == "HOLE")
				game = "HOLEM";
			else if (game == "TJUV")
				game = "TJUVM";
		}
		String file = "/juvavum/grundy/" + game + "[" + h + "x" + w + "].txt"; // alle Spielpositionen
		try {
			InputStream is = this.getClass().getResourceAsStream(file);
			new BufferedReader(new InputStreamReader(is));
		} catch (Exception e1) { // Liste mit allen Spielpositionen existiert nicht
			file="/juvavum/grundy/" + game + "[" + h + "x" + w + ", symm].txt"; // verwende Liste mit Symmetrien
		} // die Existenz zumindest einer dieser beiden Dateien, ist durch die Überprüfung des GUI gesichert
		
		long ret;

		// Position suchen
		ret = findSuccInFile(file, value);
		if (ret != -1) return getValidReturnBoard(ret,turn,true);
		b.rotate180();
		value = b.flatten();
		ret = findSuccInFile(file, value);
		if (ret != -1) return getValidReturnBoard(ret,turn,true);
		b.rotate180();
		b.flipud();
		value = b.flatten();
		ret = findSuccInFile(file, value);
		if (ret != -1) return getValidReturnBoard(ret,turn,true);
		b.flipud();
		b.fliplr();
		value = b.flatten();
		ret = findSuccInFile(file, value);
		if (ret != -1) return getValidReturnBoard(ret,turn,true);
		b.fliplr();
		if (h == w) {
			b.rotate90();
			value = b.flatten();
			ret = findSuccInFile(file, value);
			if (ret != -1) return getValidReturnBoard(ret,turn,true);
			b.rotate270();
			b.rotate270();
			value = b.flatten();
			ret = findSuccInFile(file, value);
			if (ret != -1) return getValidReturnBoard(ret,turn,true);
			b.rotate90();
			b.flipd1();
			value = b.flatten();
			ret = findSuccInFile(file, value);
			if (ret != -1) return getValidReturnBoard(ret,turn,true);
			b.flipd1();
			b.flipd2();
			value = b.flatten();
			ret = findSuccInFile(file, value);
			if (ret != -1) return getValidReturnBoard(ret,turn,true);
			b.flipd2();
		}
		return randomMove(turn);
	}

	/**
	 * @param file
	 * 				Grundywerttabelle
	 * @param value
	 * 				Binärrepräsentation
	 * @return
	 * 				guter Nachfolger (Grundywert 0) laut Grundywerttabelle
	 */
	private long findSuccInFile(String file, long value) {
		InputStream is = this.getClass().getResourceAsStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String input = "";
		try {
			while ((input = reader.readLine()) != null) {
				if (input.startsWith(value + "\t")) {
					String[] parts = input.split("\t");
					if (parts.length == 3) {
						String[] values = parts[2].split(",");
						Vector goodSucc = new Vector();
						for (int i = 0; i < values.length; i++){
							if (values[i].endsWith("]")) values[i]=values[i].substring(0, values[i].length()-1);
							if (values[i].startsWith("[")) values[i]=values[i].substring(1, values[i].length());	
							goodSucc.add(values[i].trim());
						}
						int random = (int) (Math.random() * goodSucc.size());
						return new Long(goodSucc.get(random).toString());
					}
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * @param turn
	 * 				true, wenn Spielfeld gegenüber Grundywerttabelle um 90 Grad gedreht werden muss
	 * @return
	 * 				zufälliger Nachfolger
	 */
	private Board randomMove(boolean turn) {
		if (turn) analyse.setBoard(b.turn90());
		pos = analyse.getSucc(false);
		Position succ;
		if (!pos.get(0).getChildren().isEmpty()) {
			int random = (int) (Math.random() * pos.get(0).getChildren().size());
			succ = (Position) (pos.get(0).getChildren().get(random));
		} else
			return null;
		if (turn)
			return getValidReturnBoard(succ.getValue(),true, false);
		return succ.getBoard();
	}
	
	/**
	 * Falls die Grundywerttabelle nur Äquivalenzklassen zueinander symmetrischer
	 * Positionen enthält, muss das Spielfeld eventuell noch gedreht werden.
	 * 
	 * @param ret
	 * 				Binärrepräsentation
	 * @param turn
	 * 				true, wenn Spielfeld gegenüber Grundywerttabelle um 90 Grad gedreht werden muss
	 * @param recursive
	 * 				wiederholter Aufruf
	 * @return
	 * 				gibt die Binärrepräsentation jener Position zurück, die zu der durch
	 * 				die Binärdarstellung ret dargestellten Position symmetrisch ist und eine
	 * 				gültige Nachfolgeposition der aktuellen Spielposition ist
	 */
	private Board getValidReturnBoard(long ret, boolean turn, boolean recursive){
		if (!turn){
			Board tmp = new Board(ret, h, w);
			if (analyse.isValidSucc(tmp)) return tmp;
			tmp.rotate180();
			if (analyse.isValidSucc(tmp)) return tmp;
			tmp.rotate180();
			tmp.flipud();
			if (analyse.isValidSucc(tmp)) return tmp;
			tmp.flipud();
			tmp.fliplr();
			if (analyse.isValidSucc(tmp)) return tmp;
			tmp.fliplr();
			if (h==w){
				tmp.rotate90();
				if (analyse.isValidSucc(tmp)) return tmp;
				tmp.rotate90();
				tmp.rotate270();
				if (analyse.isValidSucc(tmp)) return tmp;
				tmp.rotate270();
				tmp.flipd1();
				if (analyse.isValidSucc(tmp)) return tmp;
				tmp.flipd1();
				tmp.flipd2();
				if (analyse.isValidSucc(tmp)) return tmp;
				tmp.flipd2();
			}
		}
		else { // turn TRUE
			Board tmp = new Board(ret, h, w).turn90();
			if (analyse.isValidSucc(tmp)) return tmp;
			tmp.rotate180();
			if (analyse.isValidSucc(tmp)) return tmp;
			tmp.rotate180();
			tmp.flipud();
			if (analyse.isValidSucc(tmp)) return tmp;
			tmp.flipud();
			tmp.fliplr();
			if (analyse.isValidSucc(tmp)) return tmp;
			tmp.fliplr();
			if (h==w){
				tmp.rotate90();
				if (analyse.isValidSucc(tmp)) return tmp;
				tmp.rotate90();
				tmp.rotate270();
				if (analyse.isValidSucc(tmp)) return tmp;
				tmp.rotate270();
				tmp.flipd1();
				if (analyse.isValidSucc(tmp)) return tmp;
				tmp.flipd1();
				tmp.flipd2();
				if (analyse.isValidSucc(tmp)) return tmp;
				tmp.flipd2();
			}
		}
		if (recursive) return randomMove(turn);
		else return null;
	}

	/**
	 * @param error
	 * 				Fehlermeldung
	 */
	public void showError(String error) {
		JOptionPane.showMessageDialog(null, error, "Fehler",
				JOptionPane.ERROR_MESSAGE, errorIcon);
	}
}