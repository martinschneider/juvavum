package juvavum.analyse;

/**
 * @author Martin Schneider
 */
public class CramAnalysis extends BoardAnalysis{

	public CramAnalysis(int h, int w, boolean misere, boolean symmetrien,
			boolean fileOutput) {
		super(Game.CRAM, h, w, misere, symmetrien, fileOutput);
	}

	public CramAnalysis(Board b, boolean misere, boolean symmetrien,
			boolean fileOutput) {
		super(Game.CRAM, b, misere, symmetrien, fileOutput);
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
							move(b, posNr);
						}
					} else {
						if (!pos.get(caller).hasChildren(pos.get(foundPos)))
							pos.get(caller).addChild(pos.get(foundPos));
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

							move(b, posNr);
						}
					} else {
						if (!pos.get(caller).hasChildren(pos.get(foundPos)))
							pos.get(caller).addChild(pos.get(foundPos));
					}
					b.clear(i, j);
					b.clear(i + 1, j);
				}
				i++;
			}
			j++;
		}
	}
}