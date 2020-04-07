package juvavum.graph;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.nio.IntegerIdProvider;
import org.jgrapht.nio.csv.CSVExporter;
import org.jgrapht.nio.csv.CSVFormat;
import org.jgrapht.nio.csv.CSVImporter;
import org.jgrapht.util.SupplierUtil;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

public class GraphSerializer implements Serializer<Graph<Integer, SimpleEdge>> {

  @Override
  public void serialize(DataOutput2 out, Graph<Integer, SimpleEdge> graph) throws IOException {
    StringWriter writer = new StringWriter();
    new CSVExporter<Integer, SimpleEdge>(new IntegerIdProvider<Integer>(0),
        CSVFormat.ADJACENCY_LIST, ',').exportGraph(graph, writer);
    out.writeUTF(new StringWriter().toString());
  }

  @Override
  public Graph<Integer, SimpleEdge> deserialize(DataInput2 input, int available)
      throws IOException {
    VertexSupplier vertexSupplier = new VertexSupplier();
    Graph<Integer, SimpleEdge> g = new SimpleGraph<Integer, SimpleEdge>(vertexSupplier,
        SupplierUtil.createSupplier(SimpleEdge.class), false);
    String inputString = input.readUTF();
    if (inputString.isEmpty()) {
      return g;
    }
    Reader reader = new StringReader(inputString);
    new CSVImporter<Integer, SimpleEdge>(CSVFormat.ADJACENCY_LIST, ',').importGraph(g, reader);
    return GraphUtils.toCanonicalForm(g);
  }

}
