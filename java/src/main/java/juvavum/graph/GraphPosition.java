package juvavum.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import juvavum.analyse.Board;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;

public class GraphPosition extends Board {
  private List<Graph<Integer, SimpleEdge>> components = new ArrayList<>();
  private Set<GraphPosition> children = new HashSet<>();

  public GraphPosition(
      Graph<Integer, SimpleEdge> graph, boolean splitIntoComponents, boolean isomorphisms) {
    if (splitIntoComponents) {
      for (Graph<Integer, SimpleEdge> component :
          new BiconnectivityInspector<Integer, SimpleEdge>(graph).getConnectedComponents()) {
        if (component.vertexSet().size() > 1) {
          components.add((isomorphisms) ? GraphUtils.toCanonicalForm(component) : component);
        }
      }
    } else {
      components.add(graph);
    }
  }

  public List<Graph<Integer, SimpleEdge>> getComponents() {
    return components;
  }

  public Set<GraphPosition> getChildren() {
    return children;
  }

  public void addChild(GraphPosition child) {
    children.add(child);
  }

  // equals and hashcode depend only on components!

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((components == null) ? 0 : components.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    GraphPosition other = (GraphPosition) obj;
    if (components == null) {
      if (other.components != null) {
        return false;
      }
    } else if (!components.equals(other.components)) {
      return false;
    }
    return true;
  }
}
