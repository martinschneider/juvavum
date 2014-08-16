package juvavum.util;

import java.util.Enumeration;
import java.util.Vector;

import juvavum.analyse.Position;

/**
 * Einfache Repräsentation eines Graphen
 * 
 * @author Martin Schneider
 */
public class SimpleGraph {

	private Vector vertices;

	/**
	 * Konstruktor
	 */
	public SimpleGraph() {
		vertices = new Vector();
	}

	/**
	 * Ecke hinzfügen
	 * 
	 * @param vertex
	 *            Ecke
	 */
	public void addVertex(Vertex vertex) {
		boolean found = false;
		for (Enumeration e1 = vertices.elements(); e1.hasMoreElements();) {
			if (((Vertex) e1.nextElement()).getName().equals(vertex.getName()))
				found = true;
		}
		if (!found)
			vertices.add(vertex);
	}

	/**
	 * Ecke entfernen
	 * 
	 * @param vertex
	 *            Ecke
	 */
	public void removeEdge(Vertex vertex) {
		vertices.remove(vertex);
	}

	/**
	 * @param vertex
	 *            Ecke
	 * @return true, wenn der Graph die Ecke vertex enthält
	 */
	public boolean hasEdge(Vertex vertex) {
		return vertices.contains(vertex);
	}

	/**
	 * @return Vector aller Ecken
	 */
	public Vector getVertices() {
		return vertices;
	}

	/**
	 * Markierung für Algorithmus entfernen
	 */
	public void unmark() {
		for (Enumeration e1 = vertices.elements(); e1.hasMoreElements();) {
			((Vertex) e1.nextElement()).setVisited(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String output = "{ ";
		String output2 = "{ ";
		for (Enumeration e1 = vertices.elements(); e1.hasMoreElements();) {
			Vertex sn = (Vertex) e1.nextElement();
			output += sn.getName() + " ";
			for (Enumeration e2 = sn.getSucc().elements(); e2.hasMoreElements();) {
				Vertex sn2 = (Vertex) e2.nextElement();
				output2 += "{" + sn.getName() + "->" + sn2.getName() + "} ";
			}
		}
		return output += "}\n" + output2 + "}";
	}

	/**
	 * @param name
	 * @return Ecke mit Namen name
	 */
	public Vertex getVertex(String name) {
		for (Enumeration e1 = vertices.elements(); e1.hasMoreElements();) {
			Vertex v = (Vertex) e1.nextElement();
			if (v.getName().equals(name))
				return v;
		}
		return null;
	}

}
