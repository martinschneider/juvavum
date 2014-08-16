package juvavum.analyse;

import juvavum.util.List;

/**
 * Analyseprogramm für Triomino Juvavum
 * 
 * @author Martin Schneider
 */
public class TJGameAnalyse implements GameAnalyse {

	public List pos = new List();

	private boolean symmetrien = false;
	Board b;

	/**
	 * @param b
	 * 			Spielbrett
	 */
	public TJGameAnalyse(Board b) {
		this.b = b;
	}

	/* (non-Javadoc)
	 * @see juvavum.analyse.GameAnalyse#setBoard(juvavum.analyse.Board)
	 */
	public void setBoard(Board b) {
		this.b = b;
	}

	/* (non-Javadoc)
	 * @see juvavum.analyse.GameAnalyse#getBoard()
	 */
	public Board getBoard() {
		return b;
	}

	/* (non-Javadoc)
	 * @see juvavum.analyse.GameAnalyse#getSucc(boolean)
	 */
	public List getSucc(boolean recursive) {
		pos = new List();
		pos.add(new Position(b));
		move(b, 0, recursive);
		return pos;
	}

	/* (non-Javadoc)
	 * @see juvavum.analyse.GameAnalyse#getFirstSucc()
	 */
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

	/* (non-Javadoc)
	 * @see juvavum.analyse.GameAnalyse#isValidSucc(juvavum.analyse.Board)
	 */
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
	 * @param recursive
	 * 			  rekursiv weitersuchen
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
	 * @param recursive
	 * 			  rekursiv weitersuchen
	 */
	private void moveColumn(Board b, int caller, boolean recursive) {
		int posNr = 0;
		Position tmp = null;
		int i, j;
		int foundPos = -1;
		i = j = 1;
		while (i <= b.getWidth()) {
			j = 1;
			while (j + 2 <= b.getHeight()) {
				if (b.isFree(i, j) && b.isFree(i, j + 1) && b.isFree(i, j + 2)) {
					// free pos found
					b.set(i, j);
					b.set(i, j + 1);
					b.set(i, j + 2);
					foundPos = pos.search(b.flatten());
					tmp = new Position(b);
					if (symmetrien && foundPos == -1) {
						foundPos = findSymmetricPosition(b);
					}
					if (foundPos == -1) {
						if (!pos.get(caller).hasChildren(tmp)) {
							posNr = pos.add(tmp);
							pos.get(caller).addChild(pos.get(posNr));
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
					b.clear(i, j + 1);
					b.clear(i, j + 2);
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
	 * @param recursive
	 * 			  rekursiv weitersuchen
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
			while (i + 2 <= b.getWidth()) {
				if (b.isFree(i, j) && b.isFree(i + 1, j) && b.isFree(i + 2, j)) {
					// free pos found
					b.set(i, j);
					b.set(i + 1, j);
					b.set(i + 2, j);
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
					b.clear(i + 1, j);
					b.clear(i + 2, j);
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
	 * @param colNr
	 *            Spaltennummer
	 * @param recursive
	 * 			  rekursiv weitersuchen
	 */
	private void moveCurrentColumn(Board b, int caller, int colNr,
			boolean recursive) {
		Position tmp = null;
		int j = 0;
		int foundPos = -1;
		int posNr = 0;
		while (j + 2 <= b.getHeight()) {
			if (b.isFree(colNr, j) && (b.isFree(colNr, j + 1))
					&& (b.isFree(colNr, j + 2))) {
				// free pos found
				b.set(colNr, j);
				b.set(colNr, j + 1);
				b.set(colNr, j + 2);
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
				b.clear(colNr, j + 1);
				b.clear(colNr, j + 2);
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
	 * @param rowNr
	 *            Zeilennummer
	 */
	private void moveCurrentRow(Board b, int caller, int rowNr,
			boolean recursive) {
		int j = 0;
		int posNr = 0;
		int foundPos = -1;
		Position tmp = null;
		while (j + 2 <= b.getWidth()) {
			if (b.isFree(j, rowNr) && b.isFree(j + 1, rowNr)
					&& b.isFree(j + 2, rowNr)) {
				// free pos found
				b.set(j, rowNr);
				b.set(j + 1, rowNr);
				b.set(j + 2, rowNr);
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
				b.clear(j + 1, rowNr);
				b.clear(j + 2, rowNr);
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
