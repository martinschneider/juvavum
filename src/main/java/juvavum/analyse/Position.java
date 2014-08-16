package juvavum.analyse;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * @author Martin Schneider
 */
public class Position implements Comparable<Position> {
	private static final String FILENAME = "tmp.txt";
	long value;
	int h;
	int w;
	int grundy = -1;
	Position parent = null;
	List<Position> children = new ArrayList<Position>();

	public Position(Board b) {
		value = b.flatten();
		h = b.getHeight();
		w = b.getWidth();
	}

	public Position(Board b, int h, int w) {
		value = b.flatten();
		this.h = b.getHeight();
		this.w = b.getWidth();
	}

	public void addChild(Position pos) {
		children.add(pos);
	}

	public List<Position> getChildren() {
		return children;
	}

	public Board getBoard() {
		return new Board(value, h, w);
	}

	public boolean hasChildren(Position p) {
		long value = p.getValue();
		for (Position child : children) {
			if (child.getValue() == value)
				return true;
		}
		return false;
	}

	public int getHeight() {
		return h;
	}

	public int getWidth() {
		return w;
	}

	public int succCount() {
		if (children.isEmpty())
			return 0;
		int count = 0;
		for (Position child : children) {
			count += child.succCount();
		}
		return children.size() + count;
	}

	public int directSuccCount() {
		return children.size();
	}

	public String toString() {
		return value + " | " + grundy + " | " + children.size();
	}

	public void setGrundy(int grundy) {
		this.grundy = grundy;
	}

	public int getGrundy(boolean fileOutput) {
		if (grundy != -1)
			return grundy;
		if (children.isEmpty()) {
			this.grundy = 0;
			if (fileOutput)
				outputFile(false);
			return 0;
		} else {
			int i = 0;
			int grundy = -1;
			while (grundy == -1) {
				boolean found = false;
				for (Position child : children) {
					if (child.getGrundy(fileOutput) == i) {
						found = true;
						break;
					}
				}
				if (!found)
					grundy = i;
				i++;
			}
			this.grundy = grundy;
			if (fileOutput)
				outputFile(false);
			return grundy;
		}
	}

	public int getGrundyMisere(boolean fileOutput) {
		if (grundy != -1)
			return grundy;
		if (children.isEmpty()) {
			this.grundy = 1;
			if (fileOutput)
				outputFile(true);
			return 1;
		} else {
			int i = 0;
			int grundy = -1;
			while (grundy == -1) {
				boolean found = false;
				for (Position child : children) {
					if (child.getGrundyMisere(fileOutput) == i) {
						found = true;
						break;
					}
				}
				if (!found)
					grundy = i;
				i++;
			}
			this.grundy = grundy;
			if (fileOutput)
				outputFile(true);
			return grundy;
		}
	}

	public long getValue() {
		return value;
	}

	public void sortChildren() {
		Collections.sort(children);
	}

	public int compareTo(Position o) {
		long cValue = o.getValue();
		if (cValue > value)
			return -1;
		if (cValue < value)
			return 1;
		return 0;
	}

	public double getBranchingFactor() {
		return getBranching()[0] / getBranching()[1];
	}

	public int[] getBranching() {
		int[] ret = new int[2];
		int succ = 0;
		int nodeCount = 1;
		for (Position child : children) {
			succ += child.getChildren().size();
			nodeCount++;
		}
		ret[0] = succ;
		ret[1] = nodeCount;
		return ret;
	}

	private Vector<Long> getGoodSucc(boolean misere) {
		Vector<Long> goodSucc = new Vector<Long>();
		if (!misere) {
			for (Position child : children) {
				if (child.getGrundy(false) == 0)
					goodSucc.add(child.getValue());
			}
			return goodSucc;
		} else {
			for (Position child : children) {
				if (child.getGrundyMisere(false) == 0)
					goodSucc.add(child.getValue());
			}
			return goodSucc;
		}
	}

	private void outputFile(boolean misere) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(FILENAME, true)));
		} catch (FileNotFoundException e) {
			System.out.println(e);
		}
		Vector<Long> goodSucc = null;
		if (grundy > 0) {
			goodSucc = this.getGoodSucc(misere);
		}
		try {
			if (grundy > 0 && !goodSucc.isEmpty()) {
				out.write(value + "\t" + grundy + "\t" + goodSucc.toString());
			} else
				out.write(value + "\t" + grundy);
			out.newLine();
			out.close();
		} catch (IOException e1) {
			System.out.println(e1.getMessage());
		}
	}
}