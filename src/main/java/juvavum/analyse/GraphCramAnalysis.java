package juvavum.analyse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import juvavum.bliss.GraphUtils;
import juvavum.bliss.SimpleEdge;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleGraph;

/**
 * @author Martin Schneider
 */
public class GraphCramAnalysis extends GraphAnalysis {

	Graph<Integer, SimpleEdge> graph;
	Map<Graph<Integer, SimpleEdge>, GraphPosition> allPositions = new HashMap<Graph<Integer, SimpleEdge>, GraphPosition>();

	public GraphCramAnalysis(Board b, boolean misere, boolean isomorphisms,
			boolean fileOutput) {
		super(Game.CRAM, b, misere, isomorphisms, fileOutput);
		this.graph = b.toGraph();
		analyze(graph, allPositions);
		printResults();
	}

	public GraphCramAnalysis(int h, int w, boolean misere,
			boolean isomorphisms, boolean fileOutput) {
		this(new Board(h, w), misere, isomorphisms, fileOutput);
	}

	public void analyze(Graph<Integer, SimpleEdge> graph,
			Map<Graph<Integer, SimpleEdge>, GraphPosition> allPositions) {
		GraphPosition position;
		if (!allPositions.containsKey(graph)) {
			allPositions.put(graph, new GraphPosition());
		}
		position = allPositions.get(graph);
		for (SimpleEdge edge : graph.edgeSet()) {
			Graph<Integer, SimpleEdge> child = new SimpleGraph<Integer, SimpleEdge>(
					SimpleEdge.class);
			Graphs.addGraph(child, graph);
			child.removeEdge(graph.getEdgeSource(edge),
					graph.getEdgeTarget(edge));
			child.removeVertex(graph.getEdgeSource(edge));
			child.removeVertex(graph.getEdgeTarget(edge));
			if (isIsomorphisms()) {
				child = GraphUtils.toCanonicalForm(child);
			}
			boolean alreadyDone = false;
			if (allPositions.containsKey(child)) {
				alreadyDone = true;
			} else {
				allPositions.put(child, new GraphPosition());
			}
			if (position.addChild(allPositions.get(child)) && !alreadyDone) {
				analyze(child, allPositions);
			}

		}
	}

	void printResults() {
		int grundyValue = 0;
		if (isMisere())
			grundyValue = allPositions.get(graph).getGrundyMisere(
					isFileOutput());
		else
			grundyValue = allPositions.get(graph).getGrundy(isFileOutput());
		System.out.print("The g-value of the starting position is "
				+ grundyValue + ". The ");
		if (grundyValue > 0)
			System.out.print("first ");
		else
			System.out.print("second ");
		System.out.println("player can always win.");
		System.out.print("Number of positions");
		if (isIsomorphisms())
			System.out.print(" (isomorphisms considered)");
		System.out.println(": " + allPositions.size());
		int help = 0;
		for (GraphPosition position : allPositions.values()) {
			help += position.getChildren().size();
		}
		double avgBranching = (double) help / allPositions.size();

		getTimer().end();
		String time = getTimer().getElapsedTimeString();
		System.out.println("Duration: " + time);

		System.out.println("Average branching factor: "
				+ Math.round(avgBranching * 100.) / 100. + "\n");

		if (isFileOutput()) {
			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(FILENAME, true)));
			} catch (FileNotFoundException e) {
				System.out.println(e);
			}
			try {
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat formater = new SimpleDateFormat();
				out.write("End of calculation: "
						+ formater.format(cal.getTime()));
				out.newLine();
				out.newLine();
				out.close();
			} catch (IOException e) {
				System.out.println(e);
			}
			File file = new File(FILENAME);
			if (!isMisere())
				file.renameTo(new File(getGameName() + ".txt"));
			else
				file.renameTo(new File(getGameName() + ".txt"));
		}
	}

	@Override
	String getGameName() {
		String gameName;
		gameName = getGame().name() + "[" + getH() + "x" + getW();
		if (isMisere())
			gameName = gameName + ", misere";
		if (isIsomorphisms())
			gameName = gameName + ", isomorphisms";
		gameName = gameName + "]";
		return gameName;
	}
}
