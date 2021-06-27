package juvavum.analyse;

import java.util.Set;

/** @author Martin Schneider, mart.schneider@gmail.com */
public class JUVAnalysisNormalForm extends JUVAnalysis {

  public JUVAnalysisNormalForm(Board b, boolean misere) {
    super(b, misere);
  }

  @Override
  protected void addBoard(Board board, Set<Board> children) {
    children.add(new Board(new Board(board).toNormalForm()));
  }
}
