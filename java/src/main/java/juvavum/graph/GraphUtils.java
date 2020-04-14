package juvavum.graph;

import java.util.Map.Entry;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;

/** @author Martin Schneider, mart.schneider@gmail.com */
public class GraphUtils {
  public static Graph<Integer, SimpleEdge> bliss2jgrapht(BlissGraph<Integer> input) {
    Graph<Integer, SimpleEdge> result = new SimpleGraph<Integer, SimpleEdge>(SimpleEdge.class);

    for (Entry<Integer, BlissGraph<Integer>.Vertex> entry : input.getVertices().entrySet()) {
      BlissGraph<Integer>.Vertex vertex = entry.getValue();
      result.addVertex(vertex.id);
      for (BlissGraph<Integer>.Vertex vertex1 : vertex.edges) {
        result.addVertex(vertex1.id);
        result.addEdge(vertex.id, vertex1.id);
      }
    }
    return result;
  }

  public static BlissGraph<Integer> jgrapht2bliss(Graph<Integer, SimpleEdge> input) {
    BlissGraph<Integer> result = new BlissGraph<Integer>();

    for (Integer vertex : input.vertexSet()) {
      result.add_vertex(vertex);
    }
    for (SimpleEdge edge : input.edgeSet()) {
      result.add_edge(input.getEdgeSource(edge), input.getEdgeTarget(edge));
    }
    return result;
  }

  public static Graph<Integer, SimpleEdge> toCanonicalForm(Graph<Integer, SimpleEdge> input) {
    BlissGraph<Integer> blissGraph = jgrapht2bliss(input);
    blissGraph.find_automorphisms(null, null);
    return bliss2jgrapht(blissGraph.relabel(blissGraph.canonical_labeling()));
  }
}
