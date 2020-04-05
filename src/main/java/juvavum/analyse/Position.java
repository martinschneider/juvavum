package juvavum.analyse;

/** @author Martin Schneider, mart.schneider@gmail.com */
public class Position {
  private Board board;

  public Position() {}

  public Position(Board board) {
    this.board = board;
  }

  // equals and hashcode depend only on board.flatten()!

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((board == null) ? 0 : Long.valueOf(board.flatten()).hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Position other = (Position) obj;
    if (board == null) {
      if (other.board != null) {
        return false;
      }
    } else if (board.flatten() != other.board.flatten()) {
      return false;
    }
    return true;
  }

  public Board getBoard() {
    return board;
  }

  @Override
  public String toString() {
    return board.toString();
  }
}
