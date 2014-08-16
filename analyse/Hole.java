package juvavum.analyse;

/**
 * Repräsentierung eines Lochs in einem (Domino-)Juvavum Spielfelds.
 * 
 * @author Martin Schneider
 */
public class Hole {

	private int x; // x-Koordinate des Lochs
	
	private int y; // y-Koordinate des Lochs

	private int length; // Länge des Lochs
	
	private boolean row; // true, wenn Loch in Zeile; false, wenn Loch in Spalte

	/**
	 * @param x x-Koordinate des Lochs
	 * @param y y-Koordinate des Lochs
	 * @param length Länge des Lochs
	 * @param row true, wenn Loch in Zeile; false, wenn Loch in Spalte
	 */
	public Hole(int x, int y, int length, boolean row) {
		this.x = x;
		this.y=y;
		this.row=row;
		this.length = length;
	}

	/**
	 * @return x-Koordinate des Lochs
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return y-Koordinate des Lochs
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return Länge des Lochs
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * @return true, wenn Loch in Zeile; false, wenn Loch in Spalte
	 */
	public boolean getRow() {
		return row;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String rowStr="";
		if (row) rowStr="row";
		else rowStr="col";
		return "[("+x+","+y+"), "+length+", "+rowStr+"]";
	}
}