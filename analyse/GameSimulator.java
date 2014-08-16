package juvavum.analyse;

import juvavum.game.players.Player;

/**
 * Programm zur Simulation von Juvavum-Spielen.
 * 
 * Bietet die Möglichkeit zwei Computergegner wiederholt
 * gegeneinander antreten zu lassen.
 * 
 * @author Martin Schneider
 */
public class GameSimulator {
	private Player p1;

	private int p1Wins;
	
	private int p1WinsAsFirst;
	private int p1WinsAsSecond;
	private int p2WinsAsFirst;
	private int p2WinsAsSecond;

	private Player p2;

	private int p2Wins;

	private int firstPlayerWins;

	private int secondPlayerWins;
	
	private int moves;
	
	private int games;

	private Board b;

	public GameSimulator(Board b, Player p1, Player p2, int games) {
		this.b = b;
		this.p1 = p1;
		this.p2 = p2;
		this.games=games;
		for (int i = 1; i <= games; i++) {
			int winner = playGameP1First();
			if (winner == 1) {
				p1Wins++;
				p1WinsAsFirst++;
				firstPlayerWins++;
			} else {
				p2Wins++;
				p2WinsAsSecond++;
				secondPlayerWins++;
			}

			winner = playGameP2First();
			if (winner == 1) {
				p1Wins++;
				p1WinsAsSecond++;
				secondPlayerWins++;
			} else {
				p2Wins++;
				p2WinsAsFirst++;
				firstPlayerWins++;
			}
		}
		System.out.println("1. Spieler gewinnt: "+ firstPlayerWins+ " | " + "2. Spieler gewinnt: "+secondPlayerWins);
		System.out.println(p1.getName()+" gewinnt: " + p1Wins + " ("+p1WinsAsFirst+", "+p1WinsAsSecond+") | " + p2.getName()+" gewinnt: " +p2Wins+ " ("+p2WinsAsFirst+", "+p2WinsAsSecond+")");
	}

	public int playGameP1First() {
		b.fill();
		Board succ;
		boolean gameOver = false;
		while (gameOver == false) {
			p1.setBoard(b);
			succ = p1.getSucc();
			if (succ != null){
				b = succ;
				moves++;}
			else {
				gameOver = true;
				return 2;
			}
			p2.setBoard(b);
			succ = p2.getSucc();
			if (succ != null){
				b = succ;
				moves++;}
			else {
				gameOver = true;
				return 1;
			}
		}
		return 0;
	}

	public int playGameP2First() {
		b.fill();
		Board succ;
		boolean gameOver = false;
		while (gameOver == false) {
			p2.setBoard(b);
			succ = p2.getSucc();
			if (succ != null){
				b = succ;
				moves++;}
			else {
				gameOver = true;
				return 1;
			}
			p1.setBoard(b);
			succ = p1.getSucc();
			if (succ != null){
				b = succ;
				moves++;}
			else {
				gameOver = true;
				return 2;
			}
		}
		return 0;
	}
	
	public int getP1Wins(){
		return p1Wins;
	}
	
	public int getMoves(){
		return moves/(2*games);
	}
	
}
