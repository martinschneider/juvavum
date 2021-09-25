package juvavum.analyse;

import java.util.Set;

/** @author Martin Schneider, mart.schneider@gmail.com */
public class CRAMAnalysis extends AbstractAnalysis {

  public static final long H_DOMINO = 3L;
  public static long V_DOMINO;

  public CRAMAnalysis(Game game, Board b, boolean misere) {
    super(game, b, misere);
    V_DOMINO = 1L | 1 << b.w;
  }

  public CRAMAnalysis(Board b, boolean misere) {
    super(Game.CRAM, b, misere);
    V_DOMINO = 1L | 1 << b.w;
  }

  protected Set<Board> rowMoves(Board b, Set<Board> children) {
    for (int j = 0; j < b.h; j++) {
      children = rowMovesInRow(j, b, children);
    }
    return children;
  }

  protected Set<Board> rowMovesInRow(int j, Board b, Set<Board> children) {
    for (int i = 0; i < b.w - 1; i++) {
      if (b.isFree(i, j, H_DOMINO)) {
        Board b1 = new Board(b);
        b1.placeShape(i, j, H_DOMINO);
        addBoard(b1, children);
      }
    }
    return children;
  }

  protected Set<Board> columnMoves(Board b, Set<Board> children) {
    for (int i = 0; i < b.w; i++) {
      columnMovesInColumn(i, b, children);
    }
    return children;
  }

  protected Set<Board> columnMovesInColumn(int i, Board b, Set<Board> children) {
    for (int j = 0; j < b.h - 1; j++) {
      if (b.isFree(i, j) && b.isFree(i, j + 1)) {
        Board b1 = new Board(b);
        b1.placeShape(i, j, V_DOMINO);
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
