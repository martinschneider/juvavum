package juvavum.analyse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** @author Martin Schneider, mart.schneider@gmail.com */
public abstract class AbstractAnalysis extends ResultsPrinter implements Analysis {

  protected Map<Board, Integer> grundyMap = new HashMap<>();
  protected Map<Long, Set<Long>> winningMovesMap = new HashMap<>();

  public AbstractAnalysis(Game game, Board b, boolean misere) {
    super(game, b, misere);
    System.out.println("\n" + getGameName());
  }

  public void setBoard(Board b) {
    this.b = b;
  }

  public void analyse() {
    timer.start();
    printResults(grundy());
  }

  public int grundy() {
    return grundy(b);
  }

  protected int grundy(Board board) {
    Integer gValue = grundyMap.get(board);
    Set<Board> children = new HashSet<>();
    if (gValue != null) {
      return gValue;
    }
    addChildren(board, children);
    if (children.isEmpty()) {
      gValue = (misere) ? 1 : 0;
      grundyMap.put(board, gValue);
      return gValue;
    }
    gValue = mex(children);
    grundyMap.put(board, gValue);
    Set<Long> winningMoves = new HashSet<>();
    for (Board b : children) {
      if (grundyMap.get(b) == 0) {
        winningMoves.add(b.flatten());
      }
    }
    winningMovesMap.put(board.flatten(), winningMoves);
    return gValue;
  }

  public abstract Set<Board> addChildren(Board b, Set<Board> children);

  protected int mex(Set<Board> positions) {
    int i = 0;
    int mex = -1;
    while (mex == -1) {
      boolean found = false;
      for (Board position : positions) {
        int j = grundy(position);
        if (j == i) {
          found = true;
          break;
        }
      }
      if (!found) {
        mex = i;
      }
      i++;
    }
    return mex;
  }

  @Override
  protected int numberOfBoards() {
    return grundyMap.size();
  }

  @Override
  public Map<Long, Set<Long>> winningMoves() {
    return winningMovesMap;
  }

  public Map<Board, Integer> getGrundyMap() {
    return grundyMap;
  }
}
