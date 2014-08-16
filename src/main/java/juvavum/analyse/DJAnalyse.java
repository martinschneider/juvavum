package juvavum.analyse;

/**
 * @author Martin Schneider
 */
public class DJAnalyse extends Analyse {

	public DJAnalyse(int h, int w, boolean misere, boolean symmetrien,
			boolean fileOutput) {
		super(Game.DJUV, h, w, misere, symmetrien, fileOutput);
	}

	public DJAnalyse(Board b, boolean misere, boolean symmetrien,
			boolean fileOutput) {
		super(Game.DJUV, b, misere, symmetrien, fileOutput);
	}

	void move(Board b, int caller) {
		moveRow(b, caller);
		moveColumn(b, caller);
	}

	private void moveColumn(Board b, int caller) {
		int posNr = 0;
		Position tmp = null;
		int i, j;
		int foundPos = -1;
		i = j = 1;
		while (i <= b.getWidth()) {
			j = 1;
			while (j + 1 <= b.getHeight()) {
				if (b.isFree(i, j) && b.isFree(i, j + 1)) {
					b.set(i, j);
					b.set(i, j + 1);
					foundPos = pos.search(b.flatten());
					tmp = new Position(b);
					if (isSymmetrien() && foundPos == -1) {
						foundPos = findSymmetricPosition(b);
					}
					if (foundPos == -1) {
						if (!pos.get(caller).hasChildren(tmp)) {
							posNr = pos.add(tmp);
							pos.get(caller).addChild(pos.get(posNr));
							moveCurrentColumn(b, caller, i);
							move(b, posNr);

						}
					} else {
						if (!pos.get(caller).hasChildren(pos.get(foundPos)))
							pos.get(caller).addChild(pos.get(foundPos));
						moveCurrentColumn(b, caller, i);
					}

					b.clear(i, j);
					b.clear(i, j + 1);
				}
				j++;
			}
			i++;
		}
	}

	private void moveRow(Board b, int caller) {
		int posNr = 0;
		Position tmp = null;
		int i, j;
		int foundPos = -1;
		i = 1;
		j = 1;
		while (j <= b.getHeight()) {
			i = 1;
			while (i + 1 <= b.getWidth()) {
				if (b.isFree(i, j) && b.isFree(i + 1, j)) {
					b.set(i, j);
					b.set(i + 1, j);
					foundPos = pos.search(b.flatten());
					tmp = new Position(b);
					if (isSymmetrien() && foundPos == -1) {
						foundPos = findSymmetricPosition(b);
					}
					if (foundPos == -1) {
						if (!pos.get(caller).hasChildren(tmp)) {
							posNr = pos.add(tmp);
							pos.get(caller).addChild(pos.get(posNr));

							moveCurrentRow(b, caller, j);
							move(b, posNr);
						}
					} else {
						if (!pos.get(caller).hasChildren(pos.get(foundPos)))
							pos.get(caller).addChild(pos.get(foundPos));
						moveCurrentRow(b, caller, j);
					}
					b.clear(i, j);
					b.clear(i + 1, j);
				}
				i++;
			}
			j++;
		}
	}

	private void moveCurrentColumn(Board b, int caller, int colNr) {
		Position tmp = null;
		int j = 0;
		int foundPos = -1;
		int posNr = 0;
		while (j + 1 <= b.getHeight()) {
			if (b.isFree(colNr, j) && (b.isFree(colNr, j + 1))) {
				b.set(colNr, j);
				b.set(colNr, j + 1);
				tmp = new Position(b);
				foundPos = pos.search(b.flatten());
				if (isSymmetrien() && foundPos == -1) {
					foundPos = findSymmetricPosition(b);
				}
				if (foundPos == -1) {
					if (!pos.get(caller).hasChildren(tmp)) {
						posNr = pos.add(tmp);
						pos.get(caller).addChild(pos.get(posNr));

						moveCurrentColumn(b, caller, colNr);
						move(b, posNr);
					}
				} else {
					if (!pos.get(caller).hasChildren(pos.get(foundPos)))
						pos.get(caller).addChild(pos.get(foundPos));
					moveCurrentColumn(b, caller, colNr);
				}
				b.clear(colNr, j);
				b.clear(colNr, j + 1);
			}
			j++;
		}
	}

	private void moveCurrentRow(Board b, int caller, int rowNr) {
		int j = 0;
		int posNr = 0;
		int foundPos = -1;
		Position tmp = null;
		while (j + 1 <= b.getWidth()) {
			if (b.isFree(j, rowNr) && b.isFree(j + 1, rowNr)) {
				b.set(j, rowNr);
				b.set(j + 1, rowNr);
				foundPos = pos.search(b.flatten());
				tmp = new Position(b);
				if (isSymmetrien() && foundPos == -1) {
					foundPos = findSymmetricPosition(b);
				}
				if (foundPos == -1) {
					if (!pos.get(caller).hasChildren(tmp)) {
						posNr = pos.add(tmp);
						pos.get(caller).addChild(pos.get(posNr));
						moveCurrentRow(b, caller, rowNr);
						move(b, posNr);
					}
				} else {
					if (!pos.get(caller).hasChildren(pos.get(foundPos)))
						pos.get(caller).addChild(pos.get(foundPos));
					moveCurrentRow(b, caller, rowNr);
				}

				b.clear(j, rowNr);
				b.clear(j + 1, rowNr);
			}
			j++;
		}
	}
}