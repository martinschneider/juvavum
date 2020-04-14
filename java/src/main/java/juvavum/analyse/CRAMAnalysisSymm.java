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

  protected Map<Board, Position> positionMap = new HashMap<>();;

  @Override
  protected void addPosition(Board board, Set<Position> children) {
    Position pos = findPosition(board);
    if (pos != null) {
      children.add(pos);
    } else {
      Board copy = new Board(board);
      pos = new Position(copy);
      children.add(pos);
      positionMap.put(copy, pos);
    }
  }

  protected Position findPosition(Board board) {
    Position found = positionMap.get(board);
    if (found == null) {
      board.flipud();
      found = positionMap.get(board);
      board.flipud();
    }
    if (found == null) {
      board.fliplr();
      found = positionMap.get(board);
      board.fliplr();
    }
    if (found == null) {
      board.rotate180();
      found = positionMap.get(board);
      board.rotate180();
    }
    if (b.getHeight() == b.getWidth()) {
      if (found == null) {
        board.rotate90();
        found = positionMap.get(board);
        board.rotate270();
      }
      if (found == null) {
        board.rotate270();
        found = positionMap.get(board);
        board.rotate90();
      }
      if (found == null) {
        board.flipd1();
        found = positionMap.get(board);
        board.flipd1();
      }
      if (found == null) {
        board.flipd2();
        found = positionMap.get(board);
        board.flipd2();
      }
    }
    return found;
  }
}
