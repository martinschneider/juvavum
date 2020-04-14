package juvavum.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import juvavum.analyse.Board;
import juvavum.analyse.Game;
import juvavum.analyse.ResultsPrinter;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleGraph;

/** @author Martin Schneider, mart.schneider@gmail.com */
public class GraphCramAnalysis extends ResultsPrinter {
  private boolean isomorphisms;
  private boolean splitIntoComponents;

  private Graph<Integer, SimpleEdge> graph;
  private Map<Graph<Integer, SimpleEdge>, Integer> grundyMap = new HashMap<>();

  public GraphCramAnalysis(
      Board b, boolean misere, boolean isomorphisms, boolean splitIntoComponents) {
    super(Game.CRAM, b, misere);
    System.out.println("\n" + getGameName());
    if (misere && splitIntoComponents) {
      System.out.println("Cannot use sum theorem for misere games, fall-back to full analysis.");
      splitIntoComponents = false;
    }
    this.graph = b.toGraph();
    this.isomorphisms = isomorphisms;
    this.splitIntoComponents = splitIntoComponents;
  }

  public void analyse() {
    timer.start();
    printResults(grundy());
  }

  public GraphCramAnalysis(
      int h, int w, boolean misere, boolean isomorphisms, boolean splitIntoComponents) {
    this(new Board(h, w), misere, isomorphisms, splitIntoComponents);
  }

  private int grundy() {
    return grundy(new GraphPosition(this.graph, splitIntoComponents, isomorphisms));
  }

  private int grundy(GraphPosition position) {
    int gValue = 0;
    for (Graph<Integer, SimpleEdge> component : position.getComponents()) {
      gValue ^= grundy(component);
    }
    return gValue;
  }

  private int grundy(Graph<Integer, SimpleEdge> graph) {
    Integer gValue = grundyMap.get(graph);
    if (gValue != null) {
      return gValue;
    }
    if (graph.edgeSet().isEmpty()) {
      gValue = (misere) ? 1 : 0;
      grundyMap.put(graph, gValue);
      return gValue;
    }
    Set<GraphPosition> children = new HashSet<>();
    for (SimpleEdge edge : graph.edgeSet()) {
      Graph<Integer, SimpleEdge> child = new SimpleGraph<>(SimpleEdge.class);
      Graphs.addGraph(child, graph);
      child.removeEdge(graph.getEdgeSource(edge), graph.getEdgeTarget(edge));
      child.removeVertex(graph.getEdgeSource(edge));
      child.removeVertex(graph.getEdgeTarget(edge));
      if (isomorphisms) {
        child = GraphUtils.toCanonicalForm(child);
      }
      children.add(new GraphPosition(child, splitIntoComponents, isomorphisms));
    }
    gValue = mex(children);
    grundyMap.put(graph, gValue);
    return gValue;
  }

  private int mex(Set<GraphPosition> graphs) {
    int i = 0;
    int mex = -1;
    while (mex == -1) {
      boolean found = false;
      for (GraphPosition graph : graphs) {
        int j = grundy(graph);
        if (j == i) {
          found = true;
          break;
        }
      }
      if (!found) {
        mex = i;
      }
      i++;
    }
    return mex;
  }

  @Override
  protected int numberOfPositions() {
    return grundyMap.size();
  }
}
