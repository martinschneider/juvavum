package juvavum.graph;

import org.jgrapht.graph.DefaultEdge;

public class SimpleEdge extends DefaultEdge {

  private static final long serialVersionUID = 1L;

  @Override
  public boolean equals(Object o) {
    SimpleEdge target = (SimpleEdge) o;
    return ((Integer) getSource()).intValue() == ((Integer) target.getSource()).intValue()
        && ((Integer) getTarget()).intValue() == ((Integer) target.getTarget()).intValue();
  }

  @Override
  public int hashCode() {
    return (getSource() == null ? 0 : getSource().hashCode())
        + (getTarget() == null ? 0 : getTarget().hashCode());
  }
}
