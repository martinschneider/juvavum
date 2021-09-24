package juvavum.analyse;

import java.util.Set;

/** @author Martin Schneider, mart.schneider@gmail.com */
public class JUVAnalysis extends DJUVAnalysis {

  public JUVAnalysis(Board b, boolean misere) {
    super(Game.JUV, b, misere);
  }

  @Override
  protected Set<Board> rowMovesInRow(int j, Board b, Set<Board> children) {
    for (int i = 0; i < b.w; i++) {
      if (b.isFree(i, j)) {
        Board b1 = new Board(b);
        b1.set(i, j);
        addBoard(b1, children);
        rowMovesInRow(j, b1, children);
      }
    }
    return children;
  }

  @Override
  protected Set<Board> columnMovesInColumn(int i, Board b, Set<Board> children) {
    for (int j = 0; j < b.h; j++) {
      if (b.isFree(i, j)) {
        Board b1 = new Board(b);
        b1.set(i, j);
        addBoard(b1, children);
        columnMovesInColumn(i, b1, children);
      }
    }
    return children;
  }
}
