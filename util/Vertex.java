package juvavum.util;

import java.util.Vector;

/**
 * Repräsentation einer Ecke (in einem Graphen)
 * 
 * @author Martin Schneider
 */
public class Vertex {

	private Vector<Vertex> succ;
	private Vertex match = null;
	private String name;
	private boolean left;
	private boolean visited = false; // Markierung für Algorithmus

	/**
	 * @param name
	 * @param left
	 */
	public Vertex(String name, boolean left) {
		succ = new Vector<Vertex>();
		this.name = name;
		this.left = left;
	}

	/**
	 * @return
	 */
	public boolean getLeft() {
		return left;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param node
	 */
	public void addSucc(Vertex node) {
		succ.add(node);
	}

	/**
	 * @param node
	 */
	public void removSucc(Vertex node) {
		succ.remove(node);
	}

	/**
	 * @param node
	 * @return
	 */
	public boolean hasSucc(Vertex node) {
		return succ.contains(node);
	}

	/**
	 * @param node
	 * @return
	 */
	public boolean hasSucc(String node) {
		return false;
		// todo
	}

	/**
	 * @return
	 */
	public Vector<Vertex> getSucc() {
		return succ;
	}

	/**
	 * @return
	 */
	public boolean getVisited() {
		return visited;
	}

	/**
	 * @param visited
	 */
	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	/**
	 * @param v
	 */
	public void setMatch(Vertex v) {
		match = v;
	}

	/**
	 * @return
	 */
	public Vertex getMatch() {
		return match;
	}
}
