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

  protected Set<Position> rowMoves(Board b, Set<Position> children) {
    for (int j = 1; j <= b.getHeight(); j++) {
      children = rowMovesInRow(j, b, children);
    }
    return children;
  }

  protected Set<Position> rowMovesInRow(int j, Board b, Set<Position> children) {
    for (int i = 1; i < b.getWidth(); i++) {
      if (b.isFree(i, j) && b.isFree(i + 1, j)) {
        b.set(i, j);
        b.set(i + 1, j);
        addPosition(b, children);
        b.clear(i, j);
        b.clear(i + 1, j);
      }
    }
    return children;
  }

  protected Set<Position> columnMoves(Board b, Set<Position> children) {
    for (int i = 1; i <= b.getWidth(); i++) {
      columnMovesInColumn(i, b, children);
    }
    return children;
  }

  protected Set<Position> columnMovesInColumn(int i, Board b, Set<Position> children) {
    for (int j = 1; j < b.getHeight(); j++) {
      if (b.isFree(i, j) && b.isFree(i, j + 1)) {
        b.set(i, j);
        b.set(i, j + 1);
        addPosition(b, children);
        b.clear(i, j);
        b.clear(i, j + 1);
      }
    }
    return children;
  }

  protected void addPosition(Board board, Set<Position> children) {
    children.add(new Position(new Board(board)));
  }

  @Override
  protected Set<Position> addChildren(Board board, Set<Position> children) {
    children = rowMoves(board, children);
    children = columnMoves(board, children);
    return children;
  }
}
