package juvavum.analyse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import juvavum.graph.GraphCramAnalysis;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CRAMResultsTest {

  @ParameterizedTest
  @MethodSource("cramResults")
  public void testCram(int h, int w, int g, int pos, boolean m) {
    CRAMAnalysis a = new CRAMAnalysis(new Board(h, w), m);
    a.analyse();
    assertEquals(a.grundy(), g);
    assertEquals(a.grundyMap.size(), pos);
  }

  @ParameterizedTest
  @MethodSource("cramSymmResults")
  public void testCramSymm(int h, int w, int g, int pos, boolean m) {
    CRAMAnalysisSymm a = new CRAMAnalysisSymm(new Board(h, w), m);
    a.analyse();
    assertEquals(a.grundy(), g);
    assertEquals(a.grundyMap.size(), pos);
  }

  @ParameterizedTest
  @MethodSource("cramGraphResults")
  public void testCramGraph(int h, int w, int g, int pos, boolean m) {
    GraphCramAnalysis a = new GraphCramAnalysis(new Board(h, w), m, false, false);
    a.analyse();
    assertEquals(g, a.grundy());
    assertEquals(pos, a.grundyMap.size());
  }

  @ParameterizedTest
  @MethodSource("cramGraphIsomorphismsResults")
  public void testCramGraphIsomorphism(int h, int w, int g, int pos, boolean m) {
    GraphCramAnalysis a = new GraphCramAnalysis(new Board(h, w), m, true, false);
    a.analyse();
    assertEquals(g, a.grundy());
    assertEquals(pos, a.grundyMap.size());
  }

  @ParameterizedTest
  @MethodSource("cramGraphComponentsResults")
  public void testCramGraphComponents(int h, int w, int g, int pos, boolean m) {
    GraphCramAnalysis a = new GraphCramAnalysis(new Board(h, w), m, false, true);
    a.analyse();
    assertEquals(g, a.grundy());
    assertEquals(pos, a.grundyMap.size());
  }

  @ParameterizedTest
  @MethodSource("cramGraphIsomorphismsComponentsResults")
  public void testCramGraphIsomorphismsComponents(int h, int w, int g, int pos, boolean m) {
    GraphCramAnalysis a = new GraphCramAnalysis(new Board(h, w), m, true, true);
    a.analyse();
    assertEquals(g, a.grundy());
    assertEquals(pos, a.grundyMap.size());
  }

  private static Stream<Arguments> cramResults() {
    return Stream.of(
        Arguments.of(1, 2, 1, 2, false),
        Arguments.of(1, 3, 1, 3, false),
        Arguments.of(1, 4, 2, 5, false),
        Arguments.of(1, 5, 0, 8, false),
        Arguments.of(2, 2, 0, 6, false),
        Arguments.of(2, 3, 1, 18, false),
        Arguments.of(2, 4, 0, 54, false),
        Arguments.of(2, 5, 1, 162, false),
        Arguments.of(3, 3, 0, 98, false),
        Arguments.of(3, 4, 1, 550, false),
        Arguments.of(3, 5, 1, 3054, false),
        Arguments.of(4, 4, 0, 5700, false),
        Arguments.of(1, 2, 0, 2, true),
        Arguments.of(1, 3, 0, 3, true),
        Arguments.of(1, 4, 2, 5, true),
        Arguments.of(1, 5, 1, 8, true),
        Arguments.of(2, 2, 1, 6, true),
        Arguments.of(2, 3, 0, 18, true),
        Arguments.of(2, 4, 1, 54, true),
        Arguments.of(2, 5, 0, 162, true),
        Arguments.of(3, 3, 1, 98, true),
        Arguments.of(3, 4, 0, 550, true),
        Arguments.of(3, 5, 0, 3054, true),
        Arguments.of(4, 4, 0, 5700, true));
  }

  private static Stream<Arguments> cramSymmResults() {
    return Stream.of(
        Arguments.of(1, 2, 1, 2, false),
        Arguments.of(1, 3, 1, 2, false),
        Arguments.of(1, 4, 2, 4, false),
        Arguments.of(1, 5, 0, 5, false),
        Arguments.of(2, 2, 0, 3, false),
        Arguments.of(2, 3, 1, 9, false),
        Arguments.of(2, 4, 0, 22, false),
        Arguments.of(2, 5, 1, 56, false),
        Arguments.of(3, 3, 0, 18, false),
        Arguments.of(3, 4, 1, 164, false),
        Arguments.of(3, 5, 1, 805, false),
        Arguments.of(4, 4, 0, 778, false),
        Arguments.of(1, 2, 0, 2, true),
        Arguments.of(1, 3, 0, 2, true),
        Arguments.of(1, 4, 2, 4, true),
        Arguments.of(1, 5, 1, 5, true),
        Arguments.of(2, 2, 1, 3, true),
        Arguments.of(2, 3, 0, 9, true),
        Arguments.of(2, 4, 1, 22, true),
        Arguments.of(2, 5, 0, 56, true),
        Arguments.of(3, 3, 1, 18, true),
        Arguments.of(3, 4, 0, 164, true),
        Arguments.of(3, 5, 0, 805, true),
        Arguments.of(4, 4, 0, 778, true));
  }

  private static Stream<Arguments> cramGraphResults() {
    return Stream.of(
        Arguments.of(1, 2, 1, 2, false),
        Arguments.of(1, 3, 1, 3, false),
        Arguments.of(1, 4, 2, 5, false),
        Arguments.of(1, 5, 0, 8, false),
        Arguments.of(2, 2, 0, 6, false),
        Arguments.of(2, 3, 1, 18, false),
        Arguments.of(2, 4, 0, 54, false),
        Arguments.of(2, 5, 1, 162, false),
        Arguments.of(3, 3, 0, 98, false),
        Arguments.of(3, 4, 1, 550, false),
        Arguments.of(3, 5, 1, 3054, false),
        Arguments.of(4, 4, 0, 5700, false),
        Arguments.of(1, 2, 0, 2, true),
        Arguments.of(1, 3, 0, 3, true),
        Arguments.of(1, 4, 2, 5, true),
        Arguments.of(1, 5, 1, 8, true),
        Arguments.of(2, 2, 1, 6, true),
        Arguments.of(2, 3, 0, 18, true),
        Arguments.of(2, 4, 1, 54, true),
        Arguments.of(2, 5, 0, 162, true),
        Arguments.of(3, 3, 1, 98, true),
        Arguments.of(3, 4, 0, 550, true),
        Arguments.of(3, 5, 0, 3054, true),
        Arguments.of(4, 4, 0, 5700, true));
  }

  private static Stream<Arguments> cramGraphIsomorphismsResults() {
    return Stream.of(
        Arguments.of(1, 2, 1, 2, false),
        Arguments.of(1, 3, 1, 2, false),
        Arguments.of(1, 4, 2, 4, false),
        Arguments.of(1, 5, 0, 4, false),
        Arguments.of(2, 2, 0, 3, false),
        Arguments.of(2, 3, 1, 7, false),
        Arguments.of(2, 4, 0, 12, false),
        Arguments.of(2, 5, 1, 23, false),
        Arguments.of(3, 3, 0, 13, false),
        Arguments.of(3, 4, 1, 52, false),
        Arguments.of(3, 5, 1, 184, false),
        Arguments.of(4, 4, 0, 217, false),
        Arguments.of(1, 2, 0, 2, true),
        Arguments.of(1, 3, 0, 2, true),
        Arguments.of(1, 4, 2, 4, true),
        Arguments.of(1, 5, 1, 4, true),
        Arguments.of(2, 2, 1, 3, true),
        Arguments.of(2, 3, 0, 7, true),
        Arguments.of(2, 4, 1, 12, true),
        Arguments.of(2, 5, 0, 23, true),
        Arguments.of(3, 3, 1, 13, true),
        Arguments.of(3, 4, 0, 52, true),
        Arguments.of(3, 5, 0, 184, true),
        Arguments.of(4, 4, 0, 217, true));
  }

  private static Stream<Arguments> cramGraphIsomorphismsComponentsResults() {
    return Stream.of(
        Arguments.of(1, 2, 1, 1, false),
        Arguments.of(1, 3, 1, 1, false),
        Arguments.of(1, 4, 2, 2, false),
        Arguments.of(1, 5, 0, 3, false),
        Arguments.of(2, 2, 0, 2, false),
        Arguments.of(2, 3, 1, 4, false),
        Arguments.of(2, 4, 0, 8, false),
        Arguments.of(2, 5, 1, 13, false),
        Arguments.of(3, 3, 0, 10, false),
        Arguments.of(3, 4, 1, 34, false),
        Arguments.of(3, 5, 1, 119, false),
        Arguments.of(4, 4, 0, 132, false));
  }

  private static Stream<Arguments> cramGraphComponentsResults() {
    return Stream.of(
        Arguments.of(1, 2, 1, 1, false),
        Arguments.of(1, 3, 1, 1, false),
        Arguments.of(1, 4, 2, 3, false),
        Arguments.of(1, 5, 0, 5, false),
        Arguments.of(2, 2, 0, 5, false),
        Arguments.of(2, 3, 1, 14, false),
        Arguments.of(2, 4, 0, 36, false),
        Arguments.of(2, 5, 1, 83, false),
        Arguments.of(3, 3, 0, 69, false),
        Arguments.of(3, 4, 1, 279, false),
        Arguments.of(3, 5, 1, 1041, false),
        Arguments.of(4, 4, 0, 1881, false));
  }
}
