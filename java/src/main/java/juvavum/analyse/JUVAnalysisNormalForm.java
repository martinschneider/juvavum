package juvavum.analyse;

import java.util.Set;

/** @author Martin Schneider, mart.schneider@gmail.com */
public class JUVAnalysisNormalForm extends JUVAnalysis {

  public JUVAnalysisNormalForm(Board b, boolean misere) {
    super(b, misere);
  }

  @Override
  protected void addPosition(Board board, Set<Position> children) {
    children.add(new Position(new Board(board).toNormalForm()));
  }
}
