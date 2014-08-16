package juvavum.analyse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import juvavum.util.List;
import juvavum.util.Timing;

public abstract class Analyse {

	public List pos = new List();

	private boolean misere;

	private boolean symmetrien;

	private int w;

	private int h;

	private Board b;

	private Timing timer;

	private boolean fileOutput;

	private Game game;

	final String FILENAME = "tmp.txt";

	public Analyse(Game game, int h, int w, boolean misere, boolean symmetrien,
			boolean fileOutput) {
		this(game, new Board(h, w), misere, symmetrien, fileOutput);
	}

	public Analyse(Game game, Board b, boolean misere, boolean symmetrien,
			boolean fileOutput) {
		this.game = game;
		this.b = b;
		this.misere = misere;
		this.symmetrien = symmetrien;
		this.h = b.getHeight();
		this.w = b.getWidth();
		this.fileOutput = fileOutput;
		if (fileOutput)
			printHeaderToFile();

		System.out.println("\n" + getGameName());

		// Timer erzeugen und starten
		timer = new Timing();
		timer.begin();

		// Spielfeld und Liste der Positionen erzeugen
		pos = new List();
		pos.add(new Position(b));

		// Analyse starten
		move(b, 0);

		// Ergebnisse ausgeben
		printResults();

	}

	abstract void move(Board b, int caller);

	public boolean isMisere() {
		return misere;
	}

	public void setMisere(boolean misere) {
		this.misere = misere;
	}

	public boolean isSymmetrien() {
		return symmetrien;
	}

	public void setSymmetrien(boolean symmetrien) {
		this.symmetrien = symmetrien;
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

	public int findSymmetricPosition(Board b) {
		int foundPos = pos.search(b.flatten());
		if (foundPos == -1) {
			b.flipud();
			foundPos = pos.search(b.flatten());
			b.flipud();
		}
		if (foundPos == -1) {
			b.fliplr();
			foundPos = pos.search(b.flatten());
			b.fliplr();
		}
		if (foundPos == -1) {
			b.rotate180();
			foundPos = pos.search(b.flatten());
			b.rotate180();
		}
		if (getH() == getW()) {
			if (foundPos == -1) {
				b.rotate90();
				foundPos = pos.search(b.flatten());
				b.rotate270();
			}
			if (foundPos == -1) {
				b.rotate270();
				foundPos = pos.search(b.flatten());
				b.rotate90();
			}
			if (foundPos == -1) {
				b.flipd1();
				foundPos = pos.search(b.flatten());
				b.flipd1();
			}
			if (foundPos == -1) {
				b.flipd2();
				foundPos = pos.search(b.flatten());
				b.flipd2();
			}
		}
		return foundPos;
	}

	private void printResults() {
		boolean sortChildren = false;
		if (sortChildren) {
			for (int i = 0; i < pos.getSize(); i++)
				pos.get(i).sortChildren();
		}
		int grundyValue = 0;
		if (misere)
			grundyValue = pos.get(0).getGrundyMisere(fileOutput);
		else
			grundyValue = pos.get(0).getGrundy(fileOutput);
		System.out.print("The g-value of the starting position is "
				+ grundyValue + ". The ");
		if (grundyValue > 0)
			System.out.print("first ");
		else
			System.out.print("second ");
		System.out.println("player can always win.");
		System.out.print("Number of positions");
		if (symmetrien)
			System.out.print(" (symmetries considered)");
		int posCount = pos.getSize();
		System.out.println(": " + posCount);
		int help = 0;
		for (int i = 0; i < pos.getSize(); i++)
			help += pos.get(i).directSuccCount();
		double avgBranching = (double) help / pos.getSize();

		timer.end();
		String time = timer.getElapsedTimeString();
		System.out.println("Duration: " + time);

		System.out.println("Average branching factor: "
				+ Math.round(avgBranching * 100.) / 100. + "\n");

		if (fileOutput) {
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
				out.write("End of calculation: "
						+ formater.format(cal.getTime()));
				out.newLine();
				out.newLine();
				out.close();
			} catch (IOException e) {
				System.out.println(e);
			}
			File file = new File(FILENAME);
			if (!misere)
				file.renameTo(new File(getGameName() + ".txt"));
			else
				file.renameTo(new File(getGameName() + ".txt"));
		}
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
			System.out.println(e);
		}
	}

	private String getGameName() {
		String gameName;
		gameName = game.name() + "[" + h + "x" + w;
		if (misere)
			gameName = gameName + ", misere";
		if (symmetrien)
			gameName = gameName + ", symm";
		gameName = gameName + "]";
		return gameName;
	}

}