package juvavum.util;

import java.util.ArrayList;

import juvavum.analyse.Position;

/**
 * @author Martin Schneider
 */
public class List {
	int currentsize = 0;

	java.util.List<Position> data = new ArrayList<Position>();

	/**
	 * @param p
	 *            position
	 * @return index of the position
	 */
	public int add(Position p) {
		data.add(p);
		currentsize++;
		return currentsize - 1;
	}

	/**
	 * @return length of the list
	 */
	public int getSize() {
		return currentsize;
	}

	/**
	 * @param index
	 *            index
	 * @return position at the given index
	 */
	public Position get(int index) {
		return data.get(index);
	}

	/**
	 * @param value
	 *            binary representation
	 * @return index of the position with the given binary representation
	 */
	public int search(long value) {
		int i = 0;
		for (Position position : data) {
			if (position.getValue() == value) {
				return i;
			}
			i++;
		}
		return -1;
	}
}