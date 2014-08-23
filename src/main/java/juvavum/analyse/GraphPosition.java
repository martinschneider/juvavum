package juvavum.analyse;

import java.util.HashSet;

/**
 * @author Martin Schneider
 */
public class GraphPosition {
	HashSet<GraphPosition> children;
	int grundy = -1;

	public GraphPosition() {
		children = new HashSet<GraphPosition>();
	}

	public HashSet<GraphPosition> getChildren() {
		return children;
	}

	public boolean addChild(GraphPosition child) {
		return children.add(child);
	}

	public void setGrundy(int grundy) {
		this.grundy = grundy;
	}

	public int getGrundy(boolean fileOutput) {
		if (grundy != -1)
			return grundy;
		if (children.isEmpty()) {
			this.grundy = 0;
			// if (fileOutput)
			// outputFile(false);
			return 0;
		} else {
			int i = 0;
			int grundy = -1;
			while (grundy == -1) {
				boolean found = false;
				for (GraphPosition child : children) {
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
			// if (fileOutput)
			// outputFile(false);
			return grundy;
		}
	}

	public int getGrundyMisere(boolean fileOutput) {
		if (grundy != -1)
			return grundy;
		if (children.isEmpty()) {
			this.grundy = 1;
			// if (fileOutput)
			// outputFile(true);
			return 1;
		} else {
			int i = 0;
			int grundy = -1;
			while (grundy == -1) {
				boolean found = false;
				for (GraphPosition child : children) {
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
			// if (fileOutput)
			// outputFile(true);
			return grundy;
		}
	}

}
