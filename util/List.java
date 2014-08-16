package juvavum.util;

import java.util.Enumeration;
import java.util.Vector;

import juvavum.analyse.Position;

/**
 * Klasse zur Verwaltung von Listen
 * 
 * @author Martin Schneider
 */
public class List {
	int currentsize = 0;

	Vector data = new Vector();

	/**
	 * @param p
	 * 			Position
	 * @return
	 * 			Index der hinzugefügten Position
	 */
	public int add(Position p) {
		data.add(p);
		currentsize++;
		return currentsize - 1;
	}

	/**
	 * @return
	 * 			Länge der Liste
	 */
	public int getSize() {
		return currentsize;
	}

	/**
	 * @param index
	 * 				Index
	 * @return
	 * 				Positions-Objekt an der durch index bezeichneten Stelle der liste
	 */
	public Position get(int index) {
		return (Position) data.get(index);
	}

	/**
	 * @param value
	 * 				Binärrepräsentation
	 * @return
	 * 				Index der Position mit der angegeben Binärcodierung
	 */
	public int search(long value) {
		int i = 0;
		for (Enumeration e1 = data.elements(); e1.hasMoreElements();) {
			if (((Position) e1.nextElement()).getValue() == value) {
				return i;
			}
			i++;
		}
		return -1;
	}
}