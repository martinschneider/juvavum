package juvavum.analyse;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** @author Martin Schneider, mart.schneider@gmail.com */
public class CRAMAnalysisSymm extends CRAMAnalysis {

  public CRAMAnalysisSymm(Game game, Board b, boolean misere) {
    super(game, b, misere);
  }

  public CRAMAnalysisSymm(Board b, boolean misere) {
    super(b, misere);
  }

  protected Map<Long, Position> positionMap = new HashMap<>();;

  @Override
  protected void addPosition(Board board, Set<Position> children) {
    Position pos = findPosition(board);
    if (pos != null) {
      children.add(pos);
    } else {
      Board copy = new Board(board);
      pos = new Position(copy);
      children.add(pos);
      positionMap.put(board.flatten(), pos);
    }
  }

  protected Position findPosition(Board board) {
    Position found = positionMap.get(board.flatten());
    if (found == null) {
      board.flipud();
      found = positionMap.get(board.flatten());
      board.flipud();
    }
    if (found == null) {
      board.fliplr();
      found = positionMap.get(board.flatten());
      board.fliplr();
    }
    if (found == null) {
      board.rotate180();
      found = positionMap.get(board.flatten());
      board.rotate180();
    }
    if (b.getHeight() == b.getWidth()) {
      if (found == null) {
        board.rotate90();
        found = positionMap.get(board.flatten());
        board.rotate270();
      }
      if (found == null) {
        board.rotate270();
        found = positionMap.get(board.flatten());
        board.rotate90();
      }
      if (found == null) {
        board.flipd1();
        found = positionMap.get(board.flatten());
        board.flipd1();
      }
      if (found == null) {
        board.flipd2();
        found = positionMap.get(board.flatten());
        board.flipd2();
      }
    }
    return found;
  }
}
