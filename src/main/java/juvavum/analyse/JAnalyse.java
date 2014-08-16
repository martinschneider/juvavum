package juvavum.analyse;

/**
 * @author Martin Schneider
 */
public class JAnalyse extends Analyse {

	public JAnalyse(int h, int w, boolean misere, boolean symmetrien,
			boolean fileOutput) {
		super(Game.JUV, h, w, misere, symmetrien, fileOutput);
	}

	public JAnalyse(Board b, boolean misere, boolean symmetrien,
			boolean fileOutput) {
		super(Game.JUV, b, misere, symmetrien, fileOutput);
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
			while (j <= b.getHeight()) {
				if (b.isFree(i, j)) {
					b.set(i, j);
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
			while (i <= b.getWidth()) {
				if (b.isFree(i, j)) {
					b.set(i, j);
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
		while (j <= b.getHeight()) {
			if (b.isFree(colNr, j)) {
				b.set(colNr, j);
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
			}
			j++;
		}
	}

	private void moveCurrentRow(Board b, int caller, int rowNr) {
		int j = 0;
		int posNr = 0;
		int foundPos = -1;
		Position tmp = null;
		while (j <= b.getWidth()) {
			if (b.isFree(j, rowNr)) {
				b.set(j, rowNr);
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
			}
			j++;
		}
	}
}