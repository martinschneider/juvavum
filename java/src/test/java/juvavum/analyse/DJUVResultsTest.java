package juvavum.analyse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class DJUVResultsTest {

  @ParameterizedTest
  @MethodSource("dominoJuvavumResults")
  public void testDominoJuvavum(int h, int w, int g, int pos, boolean m) {
    DJUVAnalysis a = new DJUVAnalysis(new Board(h, w), m);
    a.analyse();
    assertEquals(g, a.grundy());
    assertEquals(pos, a.grundyMap.size());
  }

  @ParameterizedTest
  @MethodSource("dominoJuvavumSymmResults")
  public void testDominoJuvavumSymm(int h, int w, int g, int pos, boolean m) {
    DJUVAnalysisSymm a = new DJUVAnalysisSymm(new Board(h, w), m);
    a.analyse();
    assertEquals(g, a.grundy());
    assertEquals(pos, a.grundyMap.size());
  }

  private static Stream<Arguments> dominoJuvavumResults() {
    return Stream.of(
        Arguments.of(1, 2, 1, 2, false),
        Arguments.of(1, 3, 1, 3, false),
        Arguments.of(1, 4, 2, 5, false),
        Arguments.of(1, 5, 2, 8, false),
        Arguments.of(2, 2, 0, 6, false),
        Arguments.of(2, 3, 1, 18, false),
        Arguments.of(2, 4, 0, 54, false),
        Arguments.of(2, 5, 1, 162, false),
        Arguments.of(3, 3, 0, 98, false),
        Arguments.of(3, 4, 1, 550, false),
        Arguments.of(3, 5, 3, 3054, false),
        Arguments.of(4, 4, 0, 5700, false),
        Arguments.of(1, 2, 0, 2, true),
        Arguments.of(1, 3, 0, 3, true),
        Arguments.of(1, 4, 2, 5, true),
        Arguments.of(1, 5, 2, 8, true),
        Arguments.of(2, 2, 1, 6, true),
        Arguments.of(2, 3, 0, 18, true),
        Arguments.of(2, 4, 1, 54, true),
        Arguments.of(2, 5, 0, 162, true),
        Arguments.of(3, 3, 1, 98, true),
        Arguments.of(3, 4, 2, 550, true),
        Arguments.of(3, 5, 3, 3054, true),
        Arguments.of(4, 4, 1, 5700, true));
  }

  private static Stream<Arguments> dominoJuvavumSymmResults() {
    return Stream.of(
        Arguments.of(1, 2, 1, 2, false),
        Arguments.of(1, 3, 1, 2, false),
        Arguments.of(1, 4, 2, 4, false),
        Arguments.of(1, 5, 2, 5, false),
        Arguments.of(2, 2, 0, 3, false),
        Arguments.of(2, 3, 1, 9, false),
        Arguments.of(2, 4, 0, 22, false),
        Arguments.of(2, 5, 1, 56, false),
        Arguments.of(3, 3, 0, 18, false),
        Arguments.of(3, 4, 1, 164, false),
        Arguments.of(3, 5, 3, 805, false),
        Arguments.of(4, 4, 0, 778, false),
        Arguments.of(1, 2, 0, 2, true),
        Arguments.of(1, 3, 0, 2, true),
        Arguments.of(1, 4, 2, 4, true),
        Arguments.of(1, 5, 2, 5, true),
        Arguments.of(2, 2, 1, 3, true),
        Arguments.of(2, 3, 0, 9, true),
        Arguments.of(2, 4, 1, 22, true),
        Arguments.of(2, 5, 0, 56, true),
        Arguments.of(3, 3, 1, 18, true),
        Arguments.of(3, 4, 2, 164, true),
        Arguments.of(3, 5, 3, 805, true),
        Arguments.of(4, 4, 1, 778, true));
  }
}
