package juvavum.graph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import juvavum.analyse.Board;
import juvavum.analyse.Game;
import juvavum.analyse.ResultsPrinter;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleGraph;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

/**
 * Analysis which stores results in a MapDB file.
 * 
 * @author Martin Schneider, mart.schneider@gmail.com
 */
public class DBCramAnalysis extends ResultsPrinter {

  private Graph<Integer, SimpleEdge> graph;
  private Map<Graph<Integer, SimpleEdge>, Integer> grundyMap;
  private DB db;

  public DBCramAnalysis(Board b, boolean misere) {
    super(Game.CRAM, b, misere);
    System.out.println("\n" + getGameName());
    this.graph = b.toGraph();
    db = DBMaker.fileDB("cram.db").make();
    String mapName = (misere) ? "misere" : "normal";
    grundyMap = db.hashMap(mapName, new GraphSerializer(), Serializer.INTEGER).createOrOpen();
  }

  public void analyse() {
    timer.start();
    try {
      printResults(grundy());
    } finally {
      db.close();
    }
  }

  private int grundy() {
    return grundy(new GraphPosition(this.graph, false, false));
  }

  private int grundy(GraphPosition position) {
    int gValue = 0;
    for (Graph<Integer, SimpleEdge> component : position.getComponents()) {
      gValue ^= grundy(component);
    }
    return gValue;
  }

  private int grundy(Graph<Integer, SimpleEdge> graph) {
    Integer gValue = grundyMap.get(GraphUtils.toCanonicalForm(graph));
    if (gValue != null) {
      return gValue;
    }
    if (graph.edgeSet().isEmpty()) {
      gValue = (misere) ? 1 : 0;
      grundyMap.put(GraphUtils.toCanonicalForm(graph), gValue);
      return gValue;
    }
    Set<GraphPosition> children = new HashSet<>();
    for (SimpleEdge edge : graph.edgeSet()) {
      Graph<Integer, SimpleEdge> child = new SimpleGraph<>(SimpleEdge.class);
      Graphs.addGraph(child, graph);
      child.removeEdge(graph.getEdgeSource(edge), graph.getEdgeTarget(edge));
      child.removeVertex(graph.getEdgeSource(edge));
      child.removeVertex(graph.getEdgeTarget(edge));
      children.add(new GraphPosition(child, false, false));
    }
    gValue = mex(children);
    grundyMap.put(GraphUtils.toCanonicalForm(graph), gValue);
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
  protected void printResults(int grundyValue) {
    System.out.print("The g-value of the starting position is " + grundyValue + ". The ");
    if (grundyValue > 0) {
      System.out.print("first ");
    } else {
      System.out.print("second ");
    }
    System.out.println("player can always win.");

    timer.stop();
    String time = timer.getElapsedTimeString();
    System.out.println("Duration: " + time);
  }

  @Override
  protected int numberOfPositions() {
    return -1;
  }
}

