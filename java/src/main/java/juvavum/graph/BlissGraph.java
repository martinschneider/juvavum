package juvavum.graph;

/*
 * @(#)BlissGraph.java
 *
 * Copyright 2007-2010 by Tommi Junttila. Released under the GNU General Public License version 3.
 */

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * An undirected graph. Vertices can be colored (with integers) and self-loops are allowed but
 * multiple edges between vertices are ignored.
 *
 * <p>Changes:
 *
 * <ul>
 *   <li>renamed to BlissGraph
 *   <li>fixed some generics related issues
 *   <li>removed reporter
 *   <li>fixed Javadoc warnings
 * </ul>
 *
 * @author Tommi Junttila
 * @author Martin Schneider, mart.schneider@gmail.com
 */
public class BlissGraph<V extends Comparable<V>> implements Comparable<BlissGraph<V>> {
  /*
   * Intermediate translator stuff for mapping bliss automorphisms back to jbliss automorphisms
   */
  protected Map<V, Integer> _bliss_map;
  protected Map<Integer, V> _bliss_map_inv;

  /* The internal JNI interface to true bliss */
  private native long create();

  private native void destroy(long true_bliss);

  protected native int _add_vertex(long true_bliss, int color);

  protected native void _add_edge(long true_bliss, int v1, int v2);

  protected native void _find_automorphisms(long true_bliss, Reporter r);

  protected native int[] _canonical_labeling(long true_bliss, Reporter r);

  protected class Vertex implements Comparable<Vertex> {
    public V id;
    public int color;
    public TreeSet<Vertex> edges;

    protected void init(V identity, int c) {
      assert c >= 0;
      id = identity;
      color = c;
      edges = new TreeSet<Vertex>();
    }

    public Vertex(V identity) {
      init(identity, 0);
    }

    public Vertex(V identity, int c) {
      assert c >= 0;
      init(identity, c);
    }

    @Override
    public int compareTo(Vertex other) throws ClassCastException {
      return id.compareTo(other.id);
    }
  }

  public class Reporter {}

  protected Map<V, Vertex> vertices;

  public Map<V, Vertex> getVertices() {
    return vertices;
  }

  public void setVertices(Map<V, Vertex> vertices) {
    this.vertices = vertices;
  }

  /** Create a new undirected graph with no vertices or edges. */
  public BlissGraph() {
    vertices = new TreeMap<V, Vertex>();
    assert vertices != null;
  }

  /** @return the number of vertices in the graph */
  public int nof_vertices() {
    return vertices.size();
  }

  /**
   * Output the graph in the graphviz dot format.
   *
   * @param stream the output stream
   */
  public void write_dot(PrintStream stream) {
    stream.println("graph G {");
    for (Map.Entry<V, Vertex> e : vertices.entrySet()) {
      V v = e.getKey();
      Vertex vertex = e.getValue();
      stream.println("v" + v + " [label=" + vertex.color + "];");
      for (Vertex vertex2 : vertex.edges) {
        if (v.compareTo(vertex2.id) <= 0) {
          stream.println("v" + vertex.id + " -- v" + vertex2.id);
        }
      }
    }
    stream.println("}");
  }

  /** The ordering between graphs */
  @Override
  public int compareTo(BlissGraph<V> other) throws ClassCastException {
    if (nof_vertices() < other.nof_vertices()) {
      return -1;
    }
    if (nof_vertices() > other.nof_vertices()) {
      return 1;
    }
    Iterator<V> i1 = vertices.keySet().iterator();
    Iterator<V> i2 = other.vertices.keySet().iterator();
    while (i1.hasNext()) {
      V name1 = i1.next();
      V name2 = i2.next();
      int i = name1.compareTo(name2);
      if (i < 0) {
        return -1;
      }
      if (i > 0) {
        return 1;
      }
      Vertex v1 = vertices.get(name1);
      Vertex v2 = other.vertices.get(name2);
      if (v1.color < v2.color) {
        return -1;
      }
      if (v1.color > v2.color) {
        return 1;
      }
      TreeSet<Vertex> edges1 = v1.edges;
      TreeSet<Vertex> edges2 = v2.edges;
      if (edges1.size() < edges2.size()) {
        return -1;
      }
      if (edges1.size() > edges2.size()) {
        return 1;
      }
      Iterator<Vertex> vi1 = edges1.iterator();
      Iterator<Vertex> vi2 = edges2.iterator();
      while (vi1.hasNext()) {
        int c = vi1.next().compareTo(vi2.next());
        if (c < 0) {
          return -1;
        }
        if (c > 0) {
          return 1;
        }
      }
    }
    return 0;
  }

  /**
   * Add a new vertex (with the default color 0) into the graph.
   *
   * @param vertex the vertex indentifier
   * @return true if the vertex was not already in the graph
   */
  public boolean add_vertex(V vertex) {
    return add_vertex(vertex, 0);
  }

  /**
   * Add a new vertex into the graph.
   *
   * @param v the vertex indentifier
   * @param color the color of the vertex (a non-negative integer)
   * @return true if the vertex was not already in the graph
   */
  public boolean add_vertex(V v, int color) {
    assert color >= 0;
    if (vertices.containsKey(v)) {
      return false;
    }
    vertices.put(v, new Vertex(v, color));
    return true;
  }

  /**
   * Delete a vertex from the graph.
   *
   * @param v the vertex indentifier
   * @return true if the vertex was in the graph
   */
  public boolean del_vertex(V v) {
    Vertex vertex = vertices.get(v);
    if (vertex == null) {
      return false;
    }
    for (Vertex vertex2 : vertex.edges) {
      vertex2.edges.remove(vertex);
    }
    vertices.remove(v);
    return true;
  }

  /**
   * Add an undirected edge between the vertices v1 and v2. If either of the vertices is not in the
   * graph, it will be added. Duplicate edges between vertices are ignored.
   *
   * @param v1 a vertex in the graph
   * @param v2 a vertex in the graph
   */
  public void add_edge(V v1, V v2) {
    Vertex vertex1 = vertices.get(v1);
    if (vertex1 == null) {
      vertex1 = new Vertex(v1);
      vertices.put(v1, vertex1);
    }
    Vertex vertex2 = vertices.get(v2);
    if (vertex2 == null) {
      vertex2 = new Vertex(v2);
      vertices.put(v2, vertex2);
    }
    vertex1.edges.add(vertex2);
    vertex2.edges.add(vertex1);
  }

  /**
   * Remove an undirected edge between the vertices v1 and v2.
   *
   * @param v1 a vertex in the graph
   * @param v2 a vertex in the graph
   */
  public void del_edge(V v1, V v2) {
    Vertex vertex1 = vertices.get(v1);
    if (vertex1 == null) {
      return;
    }
    Vertex vertex2 = vertices.get(v2);
    if (vertex2 == null) {
      return;
    }
    vertex1.edges.remove(vertex2);
    vertex2.edges.remove(vertex1);
  }

  /**
   * Copy the graph.
   *
   * @return a copy of the graph
   */
  public BlissGraph<V> copy() {
    BlissGraph<V> g2 = new BlissGraph<V>();
    for (Map.Entry<V, Vertex> e : vertices.entrySet()) {
      g2.add_vertex(e.getKey(), e.getValue().color);
    }
    for (Map.Entry<V, Vertex> e : vertices.entrySet()) {
      for (Vertex vertex2 : e.getValue().edges) {
        if (e.getValue().compareTo(vertex2) <= 0) {
          g2.add_edge(e.getKey(), vertex2.id);
        }
      }
    }
    return g2;
  }

  /**
   * Find (a generating set for) the automorphism group of the graph. If the argument reporter is
   * non-null, then a generating set of automorphisms is reported by calling its report method for
   * each generator.
   *
   * @param reporter An object implementing the Reporter interface
   * @param reporter_param The parameter passed to the Reporter object
   */
  public void find_automorphisms(Reporter reporter, Object reporter_param) {
    long bliss = create();
    assert bliss != 0;
    _bliss_map = new TreeMap<V, Integer>();
    _bliss_map_inv = new TreeMap<Integer, V>();
    for (Map.Entry<V, Vertex> e : vertices.entrySet()) {
      V v = e.getKey();
      Vertex vertex = e.getValue();
      int bliss_vertex = _add_vertex(bliss, vertex.color);
      _bliss_map.put(v, bliss_vertex);
      _bliss_map_inv.put(bliss_vertex, v);
    }
    for (Map.Entry<V, Vertex> e : vertices.entrySet()) {
      V v = e.getKey();
      Vertex vertex = e.getValue();
      for (Vertex vertex2 : vertex.edges) {
        if (v.compareTo(vertex2.id) <= 0) {
          _add_edge(bliss, _bliss_map.get(vertex.id), _bliss_map.get(vertex2.id));
        }
      }
    }
    _find_automorphisms(bliss, null);
    destroy(bliss);
    _bliss_map = null;
    _bliss_map_inv = null;
  }

  /**
   * Find the canonical labeling and the automorphism group of the graph. If the argument reporter
   * is non-null, then a generating set of automorphisms is reported by calling its report method
   * for each generator.
   *
   * @return A canonical labeling permutation
   */
  public Map<V, Integer> canonical_labeling() {
    return canonical_labeling(null, null);
  }

  /**
   * Find the canonical labeling and the automorphism group of the graph. If the argument reporter
   * is non-null, then a generating set of automorphisms is reported by calling its report method
   * for each generator.
   *
   * @param reporter An object implementing the Reporter interface
   * @param reporter_param The parameter passed to the Reporter object
   * @return A canonical labeling permutation
   */
  public Map<V, Integer> canonical_labeling(Reporter reporter, Object reporter_param) {
    long bliss = create();
    assert bliss != 0;
    _bliss_map = new TreeMap<V, Integer>();
    _bliss_map_inv = new TreeMap<Integer, V>();
    for (Map.Entry<V, Vertex> e : vertices.entrySet()) {
      V v = e.getKey();
      Vertex vertex = e.getValue();
      int bliss_vertex = _add_vertex(bliss, vertex.color);
      _bliss_map.put(v, bliss_vertex);
      _bliss_map_inv.put(bliss_vertex, v);
    }
    for (Map.Entry<V, Vertex> e : vertices.entrySet()) {
      V v = e.getKey();
      Vertex vertex = e.getValue();
      for (Vertex vertex2 : vertex.edges) {
        if (v.compareTo(vertex2.id) <= 0) {
          _add_edge(bliss, _bliss_map.get(vertex.id), _bliss_map.get(vertex2.id));
        }
      }
    }
    int[] cf = _canonical_labeling(bliss, null);
    destroy(bliss);
    TreeMap<V, Integer> labeling = new TreeMap<V, Integer>();
    for (Map.Entry<V, Integer> e : _bliss_map.entrySet()) {
      labeling.put(e.getKey(), cf[e.getValue()]);
    }
    _bliss_map = null;
    _bliss_map_inv = null;
    return labeling;
  }

  /**
   * Copy and relabel the graph. The labeling is a Map that associates each vertex in the graph into
   * new vertex.
   *
   * @param labeling the labeling to apply
   * @param <W> vertex type
   * @return the relabeled graph
   */
  public <W extends Comparable<W>> BlissGraph<W> relabel(Map<V, W> labeling) {
    assert labeling != null;
    BlissGraph<W> g2 = new BlissGraph<W>();
    for (Map.Entry<V, Vertex> e : vertices.entrySet()) {
      g2.add_vertex(labeling.get(e.getKey()), e.getValue().color);
    }
    for (Map.Entry<V, Vertex> e : vertices.entrySet()) {
      for (Vertex vertex2 : e.getValue().edges) {
        if (e.getValue().compareTo(vertex2) <= 0) {
          g2.add_edge(labeling.get(e.getKey()), labeling.get(vertex2.id));
        }
      }
    }
    return g2;
  }

  static {
    /*
     * Load the C++ library including the true bliss and the JNI interface code
     */
    System.loadLibrary("jbliss");
  }
}
