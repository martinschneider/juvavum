package juvavum.analyse;

import java.util.Set;

/** @author Martin Schneider, mart.schneider@gmail.com */
public class DJUVAnalysisSymm extends CRAMAnalysisSymm {

  public DJUVAnalysisSymm(Game g, Board b, boolean misere) {
    super(g, b, misere);
  }

  public DJUVAnalysisSymm(Board b, boolean misere) {
    super(Game.DJUV, b, misere);
  }

  @Override
  protected Set<Position> rowMovesInRow(int j, Board b, Set<Position> children) {
    for (int i = 1; i < b.getWidth(); i++) {
      if (b.isFree(i, j) && b.isFree(i + 1, j)) {
        b.set(i, j);
        b.set(i + 1, j);
        addPosition(b, children);
        rowMovesInRow(j, b, children);
        b.clear(i, j);
        b.clear(i + 1, j);
      }
    }
    return children;
  }

  @Override
  protected Set<Position> columnMovesInColumn(int i, Board b, Set<Position> children) {
    for (int j = 1; j < b.getHeight(); j++) {
      if (b.isFree(i, j) && b.isFree(i, j + 1)) {
        b.set(i, j);
        b.set(i, j + 1);
        addPosition(b, children);
        columnMovesInColumn(i, b, children);
        b.clear(i, j);
        b.clear(i, j + 1);
      }
    }
    return children;
  }
}
