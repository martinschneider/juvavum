package juvavum.analyse;

import java.util.Map;
import java.util.Set;

public interface Analysis {
  Map<Long, Set<Long>> winningMoves();

  void analyse();
}
