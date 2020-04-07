package juvavum.analyse;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import juvavum.graph.GraphSerializer;
import juvavum.graph.GraphUtils;
import juvavum.graph.SimpleEdge;
import juvavum.graph.VertexSupplier;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.nio.csv.CSVExporter;
import org.jgrapht.nio.csv.CSVImporter;
import org.jgrapht.util.SupplierUtil;

public class CloningTest {
  public static void main(String args[]) {
    Graph g = new Board(65, 3, 3).toGraph();
    GraphSerializer gs = new GraphSerializer();


    StringWriter writer = new StringWriter();
    new CSVExporter<Integer, SimpleEdge>().exportGraph(g, writer);
    String result = writer.toString();
    if (result == null) {
      result = "";
    }


    VertexSupplier vertexSupplier = new VertexSupplier();
    Graph<Integer, SimpleEdge> g1 = new SimpleGraph<Integer, SimpleEdge>(vertexSupplier,
        SupplierUtil.createSupplier(SimpleEdge.class), false);
    Reader reader = new StringReader(result);
    new CSVImporter<Integer, SimpleEdge>().importGraph(g1, reader);

    g = GraphUtils.toCanonicalForm(g);
    g1 = GraphUtils.toCanonicalForm(g1);

    System.out.println(g.hashCode());
    System.out.println(g1.hashCode());
  }
}
