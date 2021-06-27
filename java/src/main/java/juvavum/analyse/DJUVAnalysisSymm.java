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
  protected Set<Board> rowMovesInRow(int j, Board b, Set<Board> children) {
    for (int i = 1; i < b.getWidth(); i++) {
      if (b.isFree(i, j) && b.isFree(i + 1, j)) {
        Board b1 = new Board(b);
        b1.set(i, j);
        b1.set(i + 1, j);
        addBoard(b1, children);
        rowMovesInRow(j, b1, children);
      }
    }
    return children;
  }

  @Override
  protected Set<Board> columnMovesInColumn(int i, Board b, Set<Board> children) {
    for (int j = 1; j < b.getHeight(); j++) {
      if (b.isFree(i, j) && b.isFree(i, j + 1)) {
        Board b1 = new Board(b);
        b1.set(i, j);
        b1.set(i, j + 1);
        addBoard(b1, children);
        columnMovesInColumn(j, b1, children);
      }
    }
    return children;
  }
}
