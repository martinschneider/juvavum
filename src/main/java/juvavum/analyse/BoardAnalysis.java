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

/**
 * @author Martin Schneider
 */
public abstract class BoardAnalysis extends Analysis {

	public List pos = new List();

	private boolean symmetrien;

	public BoardAnalysis(Game game, int h, int w, boolean misere,
			boolean symmetrien, boolean fileOutput) {
		this(game, new Board(h, w), misere, symmetrien, fileOutput);
	}

	public BoardAnalysis(Game game, Board b, boolean misere,
			boolean symmetrien, boolean fileOutput) {
		super(game, b, misere, fileOutput);
		this.symmetrien = symmetrien;
		pos = new List();
		pos.add(new Position(b));
		move(b, 0);
		printResults();
	}

	abstract void move(Board b, int caller);

	public boolean isSymmetrien() {
		return symmetrien;
	}

	public void setSymmetrien(boolean symmetrien) {
		this.symmetrien = symmetrien;
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

	void printResults() {
		boolean sortChildren = false;
		if (sortChildren) {
			for (int i = 0; i < pos.getSize(); i++)
				pos.get(i).sortChildren();
		}
		int grundyValue = 0;
		if (isMisere())
			grundyValue = pos.get(0).getGrundyMisere(isFileOutput());
		else
			grundyValue = pos.get(0).getGrundy(isFileOutput());
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

		getTimer().end();
		String time = getTimer().getElapsedTimeString();
		System.out.println("Duration: " + time);

		System.out.println("Average branching factor: "
				+ Math.round(avgBranching * 100.) / 100. + "\n");

		if (isFileOutput()) {
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
			if (!isMisere())
				file.renameTo(new File(getGameName() + ".txt"));
			else
				file.renameTo(new File(getGameName() + ".txt"));
		}
	}

	String getGameName() {
		String gameName;
		gameName = getGame().name() + "[" + getH() + "x" + getW();
		if (isMisere())
			gameName = gameName + ", misere";
		if (symmetrien)
			gameName = gameName + ", symm";
		gameName = gameName + "]";
		return gameName;
	}

}