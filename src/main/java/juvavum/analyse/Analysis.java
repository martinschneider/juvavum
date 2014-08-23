package juvavum.analyse;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import juvavum.util.Timing;

public abstract class Analysis {

	private boolean misere;

	private int w;

	private int h;

	private Board b;

	private Timing timer;

	private boolean fileOutput;

	private Game game;

	final String FILENAME = "tmp.txt";

	public Analysis(Game game, Board b, boolean misere, boolean fileOutput) {
		this.game = game;
		this.b = b;
		this.misere = misere;
		this.w = b.getWidth();
		this.h = b.getHeight();
		this.fileOutput = fileOutput;
		if (fileOutput) {
			printHeaderToFile();
		}
		System.out.println("\n" + getGameName());
		timer = new Timing();
		timer.begin();
	}

	private void printHeaderToFile() {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(FILENAME, true)));
		} catch (FileNotFoundException e) {
			System.out.println(e);
		}
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat formater = new SimpleDateFormat();
			out.write(formater.format(cal.getTime()));
			out.newLine();
			if (!misere)
				out.write("Analysis of position " + b.flatten() + " of "
						+ getGameName());
			else
				out.write("Analysis of position " + b.flatten() + " of "
						+ getGameName());
			out.newLine();
			out.write("Pos.\tg-value");
			out.newLine();
			out.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	abstract String getGameName();

	abstract void printResults();

	public boolean isMisere() {
		return misere;
	}

	public void setMisere(boolean misere) {
		this.misere = misere;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public Board getB() {
		return b;
	}

	public void setB(Board b) {
		this.b = b;
	}

	public Timing getTimer() {
		return timer;
	}

	public void setTimer(Timing timer) {
		this.timer = timer;
	}

	public boolean isFileOutput() {
		return fileOutput;
	}

	public void setFileOutput(boolean fileOutput) {
		this.fileOutput = fileOutput;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}
}
