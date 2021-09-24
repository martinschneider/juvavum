package juvavum.analyse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class JUVResultsTest {
  @ParameterizedTest
  @MethodSource("juvavumResults")
  public void testJuvavum(int h, int w, int g, int pos, boolean m) {
    JUVAnalysis a = new JUVAnalysis(new Board(h, w), m);
    a.analyse();
    assertEquals(g, a.grundy());
    assertEquals(pos, a.grundyMap.size());
  }

  @ParameterizedTest
  @MethodSource("juvavumSymmResults")
  public void testJuvavumSymm(int h, int w, int g, int pos, boolean m) {
    JUVAnalysisSymm a = new JUVAnalysisSymm(new Board(h, w), m);
    a.analyse();
    assertEquals(g, a.grundy());
    assertEquals(pos, a.grundyMap.size());
  }

  @ParameterizedTest
  @MethodSource("juvavumNormalFormResults")
  public void testJuvavumNormalForm(int h, int w, int g, int pos, boolean m) {
    JUVAnalysisNormalForm a = new JUVAnalysisNormalForm(new Board(h, w), m);
    a.analyse();
    assertEquals(g, a.grundy());
    assertEquals(pos, a.grundyMap.size());
  }

  private static Stream<Arguments> juvavumResults() {
    return Stream.of(
        Arguments.of(1, 1, 1, 2, false),
        Arguments.of(1, 2, 2, 4, false),
        Arguments.of(1, 3, 3, 8, false),
        Arguments.of(1, 4, 4, 16, false),
        Arguments.of(1, 5, 5, 32, false),
        Arguments.of(2, 2, 0, 16, false),
        Arguments.of(2, 3, 1, 64, false),
        Arguments.of(2, 4, 0, 256, false),
        Arguments.of(2, 5, 1, 1024, false),
        Arguments.of(3, 3, 0, 512, false),
        Arguments.of(3, 4, 4, 4096, false),
        Arguments.of(1, 1, 0, 2, true),
        Arguments.of(1, 2, 2, 4, true),
        Arguments.of(1, 3, 3, 8, true),
        Arguments.of(1, 4, 4, 16, true),
        Arguments.of(1, 5, 5, 32, true),
        Arguments.of(2, 2, 0, 16, true),
        Arguments.of(2, 3, 2, 64, true),
        Arguments.of(2, 4, 1, 256, true),
        Arguments.of(2, 5, 0, 1024, true),
        Arguments.of(3, 3, 1, 512, true),
        Arguments.of(3, 4, 0, 4096, true));
  }

  private static Stream<Arguments> juvavumSymmResults() {
    return Stream.of(
        Arguments.of(1, 1, 1, 2, false),
        Arguments.of(1, 2, 2, 3, false),
        Arguments.of(1, 3, 3, 6, false),
        Arguments.of(1, 4, 4, 10, false),
        Arguments.of(1, 5, 5, 20, false),
        Arguments.of(2, 2, 0, 6, false),
        Arguments.of(2, 3, 1, 24, false),
        Arguments.of(2, 4, 0, 76, false),
        Arguments.of(2, 5, 1, 288, false),
        Arguments.of(3, 3, 0, 102, false),
        Arguments.of(3, 4, 4, 1120, false),
        Arguments.of(1, 1, 0, 2, true),
        Arguments.of(1, 2, 2, 3, true),
        Arguments.of(1, 3, 3, 6, true),
        Arguments.of(1, 4, 4, 10, true),
        Arguments.of(1, 5, 5, 20, true),
        Arguments.of(2, 2, 0, 6, true),
        Arguments.of(2, 3, 2, 24, true),
        Arguments.of(2, 4, 1, 76, true),
        Arguments.of(2, 5, 0, 288, true),
        Arguments.of(3, 3, 1, 102, true),
        Arguments.of(3, 4, 0, 1120, true));
  }

  private static Stream<Arguments> juvavumNormalFormResults() {
    return Stream.of(
        Arguments.of(1, 1, 1, 2, false),
        Arguments.of(1, 2, 2, 3, false),
        Arguments.of(1, 3, 3, 4, false),
        Arguments.of(1, 4, 4, 5, false),
        Arguments.of(1, 5, 5, 6, false),
        Arguments.of(2, 2, 0, 7, false),
        Arguments.of(2, 3, 1, 16, false),
        Arguments.of(2, 4, 0, 33, false),
        Arguments.of(2, 5, 1, 69, false),
        Arguments.of(3, 3, 0, 58, false),
        Arguments.of(3, 4, 4, 215, false),
        Arguments.of(1, 1, 0, 2, true),
        Arguments.of(1, 2, 2, 3, true),
        Arguments.of(1, 3, 3, 4, true),
        Arguments.of(1, 4, 4, 5, true),
        Arguments.of(1, 5, 5, 6, true),
        Arguments.of(2, 2, 0, 7, true),
        Arguments.of(2, 3, 2, 16, true),
        Arguments.of(2, 4, 1, 33, true),
        Arguments.of(2, 5, 0, 69, true),
        Arguments.of(3, 3, 1, 58, true),
        Arguments.of(3, 4, 0, 215, true));
  }
}
