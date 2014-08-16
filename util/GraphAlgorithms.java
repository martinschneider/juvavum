package juvavum.util;

import java.util.Enumeration;

/**
 * Graphenalgorithmen
 * 
 * @author Martin Schneider
 */
public class GraphAlgorithms {
	
	/**
	 * @param v
	 */
	public void explore(Vertex v){
		for (Enumeration e1 = v.getSucc().elements(); e1.hasMoreElements();) {
			Vertex v1=(Vertex)e1.nextElement();
			if (!v1.getVisited()){
				v1.setVisited(true);
				explore(v1);
			}
		}
	}
	
	/**
	 * @param g
	 */
	public void DFS(SimpleGraph g){
		for (Enumeration e1 = g.getVertices().elements(); e1.hasMoreElements();) {
			Vertex v=(Vertex)e1.nextElement();
			if (!v.getVisited()){
				explore(v);
			}
		}
	}
	
	/**
	 * @param v
	 * @param g
	 * @return
	 */
	public boolean invertPath(Vertex v, SimpleGraph g){
		for (Enumeration e1 = v.getSucc().elements(); e1.hasMoreElements();) {
			Vertex u=(Vertex)e1.nextElement();
			String name=u.getName();
			if (!g.getVertex(name).getVisited()){
				g.getVertex(name).setVisited(true);
				if (g.getVertex(name).getMatch()==null || invertPath(g.getVertex(g.getVertex(name).getMatch().getName()),g)){
					g.getVertex(name).setMatch(v);
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @param g
	 * @return
	 */
	public int maxMatching(SimpleGraph g){
		for (Enumeration e1 = g.getVertices().elements(); e1.hasMoreElements();) {
			Vertex v=(Vertex)(e1.nextElement());
			if (v.getLeft()){
				g.unmark();
				invertPath(v,g);
			}
		}
		int match=0;
		for (Enumeration e1 = g.getVertices().elements(); e1.hasMoreElements();) {
			Vertex v=(Vertex)(e1.nextElement());
			if (v.getMatch()!=null){
				match++;
			}
		}
		return match;
	}
}