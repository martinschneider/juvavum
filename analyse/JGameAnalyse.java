package juvavum.analyse;

import juvavum.util.List;

/**
 * Analyseprogramm für Juvavum
 * 
 * @author Martin Schneider
 */
public class JGameAnalyse implements GameAnalyse {

	public List pos = new List();
	private boolean symmetrien = false;
	Board b;

	public JGameAnalyse(Board b) {
		this.b = b;
	}

	public void setBoard(Board b) {
		this.b = b;
	}
	
	public Board getBoard(){
		return b;
	}

	public List getSucc(boolean recursive) {
		pos = new List();
		pos.add(new Position(b));
		move(b, 0, recursive);
		return pos;
	}

	public Board getFirstSucc() {
		pos = new List();
		pos.add(new Position(b));
		move(b, 0, false);
		Position succ;
		if (!pos.get(0).getChildren().isEmpty())
			succ = (Position) (pos.get(0).getChildren().get(0));
		else
			return null;
		return new Board(succ.getValue(), b.getHeight(), b.getWidth());
	}

	public boolean isValidSucc(Board succ) {
		pos = new List();
		pos.add(new Position(b));
		move(b, 0, false);
		if (pos.get(0).hasChildren(new Position(succ)))
			return true;
		succ.rotate180();
		if (pos.get(0).hasChildren(new Position(succ)))
			return true;
		succ.rotate180();
		succ.flipud();
		if (pos.get(0).hasChildren(new Position(succ)))
			return true;
		succ.flipud();
		succ.fliplr();
		if (pos.get(0).hasChildren(new Position(succ)))
			return true;
		succ.fliplr();
		if (succ.getWidth() == succ.getHeight()) {
			succ.rotate90();
			if (pos.get(0).hasChildren(new Position(succ)))
				return true;
			succ.rotate180();
			if (pos.get(0).hasChildren(new Position(succ)))
				return true;
			succ.rotate90();
			succ.flipd1();
			if (pos.get(0).hasChildren(new Position(succ)))
				return true;
			succ.flipd1();
			succ.flipd2();
			if (pos.get(0).hasChildren(new Position(succ)))
				return true;
			succ.flipd2();
		}
		return false;
	}

	/**
	 * Findet alle Nachfolger einer Spielposition und fügt Verweise zur Liste
	 * aller Positionen hinzu.
	 * 
	 * @param b
	 *            Spielfeld
	 * @param caller
	 *            Nummer der Ausgangsposition in der pos-Liste
	 */
	private void move(Board b, int caller, boolean recursive) {
		moveRow(b, caller, recursive);
		moveColumn(b, caller, recursive);
	}

	/**
	 * Findet alle Nachfolger, die durch einen Spaltenzug erreichbar sind.
	 * 
	 * @param b
	 *            Spielfeld
	 * @param caller
	 *            Nummer der Ausgangsposition in der pos-Liste
	 */
	private void moveColumn(Board b, int caller, boolean recursive) {
		int posNr = 0;
		Position tmp = null;
		int i, j;
		int foundPos = -1;
		i = j = 1;
		while (i <= b.getWidth()) {
			j = 1;
			while (j <= b.getHeight()) {
				if (b.isFree(i, j)) {
					// free pos found
					b.set(i, j);
					foundPos = pos.search(b.flatten());
					tmp = new Position(b);
					if (symmetrien && foundPos == -1) {
						foundPos = findSymmetricPosition(b);
					}
					if (foundPos == -1) {
						if (!pos.get(caller).hasChildren(tmp)) {
							posNr = pos.add(tmp);
							pos.get(caller).addChild(pos.get(posNr));
							/*
							 * graph.addVertex(pos.get(caller).toString());
							 * graph.addVertex(pos.get(posNr).toString());
							 * graph.addEdge(pos.get(caller).toString(),pos.get(posNr).toString());
							 */
							moveCurrentColumn(b, caller, i, recursive);
							if (recursive)
								move(b, posNr, recursive);

						}
					} else {
						if (!pos.get(caller).hasChildren(pos.get(foundPos)))
							pos.get(caller).addChild(pos.get(foundPos));
						moveCurrentColumn(b, caller, i, recursive);
					}

					b.clear(i, j);
				}
				j++;
			}
			i++;
		}
	}

	/**
	 * Findet alle Nachfolger, die durch einen Zeilenzug erreichbar sind.
	 * 
	 * @param b
	 *            Spielfeld
	 * @param caller
	 *            Nummer der Ausgangsposition in der pos-Liste
	 */
	private void moveRow(Board b, int caller, boolean recursive) {
		int posNr = 0;
		Position tmp = null;
		int i, j;
		int foundPos = -1;
		i = 1;
		j = 1;
		while (j <= b.getHeight()) {
			i = 1;
			while (i <= b.getWidth()) {
				if (b.isFree(i, j)) {
					// free pos found
					b.set(i, j);
					foundPos = pos.search(b.flatten());
					tmp = new Position(b);
					if (symmetrien && foundPos == -1) {
						foundPos = findSymmetricPosition(b);
					}
					if (foundPos == -1) {
						if (!pos.get(caller).hasChildren(tmp)) {
							posNr = pos.add(tmp);
							pos.get(caller).addChild(pos.get(posNr));

							moveCurrentRow(b, caller, j, recursive);
							if (recursive)
								move(b, posNr, recursive);
						}
					} else {
						if (!pos.get(caller).hasChildren(pos.get(foundPos)))
							pos.get(caller).addChild(pos.get(foundPos));
						moveCurrentRow(b, caller, j, recursive);
					}
					b.clear(i, j);
				}
				i++;
			}
			j++;
		}
	}

	/**
	 * Findet Zeilenzüge mit weiteren Dominos (in der gleichen Spalte).
	 * 
	 * @param b
	 *            Spielfeld
	 * @param caller
	 *            Nummer der Ausgangsposition in der pos-Liste
	 * @param rowNr
	 *            Zeilennummer
	 */
	private void moveCurrentColumn(Board b, int caller, int colNr,
			boolean recursive) {
		Position tmp = null;
		int j = 0;
		int foundPos = -1;
		int posNr = 0;
		while (j <= b.getHeight()) {
			if (b.isFree(colNr, j)) {
				// free pos found
				b.set(colNr, j);
				tmp = new Position(b);
				if (symmetrien && foundPos == -1) {
					foundPos = findSymmetricPosition(b);
				}
				if (foundPos == -1) {
					if (!pos.get(caller).hasChildren(tmp)) {
						posNr = pos.add(tmp);
						pos.get(caller).addChild(pos.get(posNr));

						moveCurrentColumn(b, caller, colNr, recursive);
						if (recursive)
							move(b, posNr, recursive);
					}
				} else {
					if (!pos.get(caller).hasChildren(pos.get(foundPos)))
						pos.get(caller).addChild(pos.get(foundPos));
					moveCurrentColumn(b, caller, colNr, recursive);
				}
				b.clear(colNr, j);
			}
			j++;
		}
	}

	/**
	 * Findet Zeilenzüge mit weiteren Dominos (in der gleichen Zeile).
	 * 
	 * @param b
	 *            Spielfeld
	 * @param caller
	 *            Nummer der Ausgangsposition in der pos-Liste
	 * @param columnNr
	 *            Spaltennummer
	 */
	private void moveCurrentRow(Board b, int caller, int rowNr,
			boolean recursive) {
		int j = 0;
		int posNr = 0;
		int foundPos = -1;
		Position tmp = null;
		while (j <= b.getWidth()) {
			if (b.isFree(j, rowNr)) {
				// free pos found
				b.set(j, rowNr);
				foundPos = pos.search(b.flatten());
				tmp = new Position(b);
				if (symmetrien && foundPos == -1) {
					foundPos = findSymmetricPosition(b);
				}
				if (foundPos == -1) {
					if (!pos.get(caller).hasChildren(tmp)) {
						posNr = pos.add(tmp);
						pos.get(caller).addChild(pos.get(posNr));
						moveCurrentRow(b, caller, rowNr, recursive);
						if (recursive)
							move(b, posNr, recursive);
					}
				} else {
					if (!pos.get(caller).hasChildren(pos.get(foundPos)))
						pos.get(caller).addChild(pos.get(foundPos));
					moveCurrentRow(b, caller, rowNr, recursive);
				}

				b.clear(j, rowNr);
			}
			j++;
		}
	}

	/**
	 * Sucht ob zu einer Spielposition symmetrische Positionen bereits in der
	 * Liste vorkommen.
	 * 
	 * @param b
	 *            Spielposition
	 * @return Nummer der Position (-1, falls nicht gefunden)
	 */
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
		if (b.getHeight() == b.getWidth()) {
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
}
