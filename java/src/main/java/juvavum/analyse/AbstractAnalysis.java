package juvavum.analyse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** @author Martin Schneider, mart.schneider@gmail.com */
public abstract class AbstractAnalysis extends ResultsPrinter {

  protected Map<Board, Integer> grundyMap = new HashMap<>();

  public AbstractAnalysis(Game game, Board b, boolean misere) {
    super(game, b, misere);
    System.out.println("\n" + getGameName());
  }

  protected void analyse() {
    timer.start();
    printResults(grundy());
  }

  protected int grundy() {
    return grundy(new Position(b));
  }

  protected int grundy(Position position) {
    Board board = position.getBoard();
    Integer gValue = grundyMap.get(board);
    if (gValue != null) {
      return gValue;
    }
    Set<Position> children = new HashSet<>();
    addChildren(board, children);
    if (children.isEmpty()) {
      gValue = (misere) ? 1 : 0;
      grundyMap.put(board, gValue);
      return gValue;
    }
    gValue = mex(children);
    grundyMap.put(board, gValue);
    return gValue;
  }

  protected abstract Set<Position> addChildren(Board b, Set<Position> children);

  protected int mex(Set<Position> positions) {
    int i = 0;
    int mex = -1;
    while (mex == -1) {
      boolean found = false;
      for (Position graph : positions) {
        int j = grundy(graph);
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
  protected int numberOfPositions() {
    return grundyMap.size();
  }
}
