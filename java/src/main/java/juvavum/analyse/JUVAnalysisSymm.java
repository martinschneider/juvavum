package juvavum.analyse;

import java.util.Set;

/** @author Martin Schneider, mart.schneider@gmail.com */
public class JUVAnalysisSymm extends DJUVAnalysisSymm {

  public JUVAnalysisSymm(Board b, boolean misere) {
    super(Game.JUV, b, misere);
  }

  @Override
  protected Set<Position> rowMovesInRow(int j, Board b, Set<Position> children) {
    for (int i = 1; i <= b.getWidth(); i++) {
      if (b.isFree(i, j)) {
        b.set(i, j);
        addPosition(b, children);
        rowMovesInRow(j, b, children);
        b.clear(i, j);
      }
    }
    return children;
  }

  @Override
  protected Set<Position> columnMovesInColumn(int i, Board b, Set<Position> children) {
    for (int j = 1; j <= b.getHeight(); j++) {
      if (b.isFree(i, j)) {
        b.set(i, j);
        addPosition(b, children);
        columnMovesInColumn(i, b, children);
        b.clear(i, j);
      }
    }
    return children;
  }
}
