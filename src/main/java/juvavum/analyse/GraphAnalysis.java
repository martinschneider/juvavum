package juvavum.analyse;

/**
 * @author Martin Schneider
 */
public abstract class GraphAnalysis extends Analysis {

	private boolean isomorphisms;
	
	public GraphAnalysis(Game game, Board b, boolean misere, boolean isomorphisms, boolean fileOutput) {
		super(game, b, misere, fileOutput);
		this.isomorphisms = isomorphisms;
	}

	public GraphAnalysis(Game game, int h, int w, boolean misere,
			boolean isomorphisms, boolean fileOutput) {
		this(game, new Board(h, w), misere, isomorphisms, fileOutput);
	}

	public boolean isIsomorphisms() {
		return isomorphisms;
	}

	public void setIsomorphisms(boolean isomorphisms) {
		this.isomorphisms = isomorphisms;
	}
	
}
