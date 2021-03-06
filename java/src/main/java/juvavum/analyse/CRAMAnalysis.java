package juvavum.analyse;

import java.util.Set;

/** @author Martin Schneider, mart.schneider@gmail.com */
public class CRAMAnalysis extends AbstractAnalysis {

  public CRAMAnalysis(Game game, Board b, boolean misere) {
    super(game, b, misere);
  }

  public CRAMAnalysis(Board b, boolean misere) {
    super(Game.CRAM, b, misere);
  }

  protected Set<Board> rowMoves(Board b, Set<Board> children) {
    for (int j = 1; j <= b.getHeight(); j++) {
      children = rowMovesInRow(j, b, children);
    }
    return children;
  }

  protected Set<Board> rowMovesInRow(int j, Board b, Set<Board> children) {
    for (int i = 1; i < b.getWidth(); i++) {
      if (b.isFree(i, j) && b.isFree(i + 1, j)) {
        Board b1 = new Board(b);
        b1.set(i, j);
        b1.set(i + 1, j);
        addBoard(b1, children);
      }
    }
    return children;
  }

  protected Set<Board> columnMoves(Board b, Set<Board> children) {
    for (int i = 1; i <= b.getWidth(); i++) {
      columnMovesInColumn(i, b, children);
    }
    return children;
  }

  protected Set<Board> columnMovesInColumn(int i, Board b, Set<Board> children) {
    for (int j = 1; j < b.getHeight(); j++) {
      if (b.isFree(i, j) && b.isFree(i, j + 1)) {
        Board b1 = new Board(b);
        b1.set(i, j);
        b1.set(i, j + 1);
        addBoard(b1, children);
      }
    }
    return children;
  }

  protected void addBoard(Board board, Set<Board> children) {
    children.add(board);
  }

  @Override
  public Set<Board> addChildren(Board board, Set<Board> children) {
    children = rowMoves(board, children);
    children = columnMoves(board, children);
    return children;
  }
}
