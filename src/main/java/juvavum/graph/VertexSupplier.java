package juvavum.graph;

import java.util.function.Supplier;

public class VertexSupplier implements Supplier<Integer> {

  private int id = 0;

  @Override
  public Integer get() {
    return Integer.valueOf(id++);
  }
}
