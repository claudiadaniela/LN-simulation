package io.gridplus.ln.network.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.io.ComponentAttributeProvider;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;
import org.jgrapht.io.GraphImporter;
import org.jgrapht.io.GraphMLExporter;
import org.jgrapht.io.GraphMLExporter.AttributeCategory;
import org.jgrapht.io.GraphMLExporter.AttributeType;
import org.jgrapht.io.GraphMLImporter;
import org.jgrapht.io.ImportException;
import org.jgrapht.io.IntegerComponentNameProvider;
import org.jgrapht.io.VertexProvider;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.NetworkTopology;
import io.gridplus.ln.network.factory.NetworkTopologyAbstractFactory;
import io.gridplus.ln.view.NetworkGraphView;

public final class GraphIO {

    private static GraphExporter<LNVertex, LNEdge> createExporter() {
        GraphMLExporter<LNVertex, LNEdge> exporter =
                new GraphMLExporter<>((v) -> v.getId() + "", null, new IntegerComponentNameProvider<>(), null);

        exporter.setExportEdgeWeights(true);
        exporter.registerAttribute("feePercentage", AttributeCategory.NODE, AttributeType.DOUBLE);
        exporter.registerAttribute("networkStatus", AttributeCategory.NODE, AttributeType.DOUBLE);
        exporter.registerAttribute("tokenAmount", AttributeCategory.EDGE, AttributeType.INT);
        exporter.registerAttribute("hop", AttributeCategory.NODE, AttributeType.BOOLEAN);

        ComponentAttributeProvider<LNVertex> vertexAttributeProvider =
                v -> {
                    Map<String, String> m = new HashMap<>();
                    if (v.feePercentage != 0) {
                        m.put("feePercentage", v.feePercentage + "");
                        m.put("hop", v.hop? "1":"0");
                        m.put("networkStatus", v.networkStatus.getHealthScore() + "");
                    }
                    return m;
                };
        exporter.setVertexAttributeProvider(vertexAttributeProvider);


        ComponentAttributeProvider<LNEdge> edgeAttributeProvider =
                e -> {
                    Map<String, String> m = new HashMap<>();
                    m.put("tokenAmount", e.getTotalAmount() + "");
                    return m;
                };
        exporter.setEdgeAttributeProvider(edgeAttributeProvider);

        return exporter;
    }


    private static GraphImporter<LNVertex, LNEdge> createImporter() {

        VertexProvider<LNVertex> vertexProvider = (id, attributes) -> {
            int idValue = Integer.parseInt(id);
            LNVertex v = new LNVertex(idValue);
            if (attributes.get("feePercentage") != null) {
                double feeValue = Double.parseDouble(attributes.get("feePercentage"));
                v.feePercentage =feeValue;
            }
            if (attributes.get("hop") != null) {
                boolean hopValue = attributes.get("hop").equals("1")? true: false;
                v.hop=hopValue;
            }
            if (attributes.get("networkStatus") != null) {
                double statusValue = Double.parseDouble(attributes.get("networkStatus"));
                v.networkStatus = new LNVertex.NetworkStatus(statusValue);
            }
            return v;
        };

        EdgeProvider<LNVertex, LNEdge> edgeProvider =
                (from, to, label, attributes) -> {
                    int tokenAmount = Integer.parseInt(attributes.get("tokenAmount"));
                    LNEdge edge = new LNEdge();
                    edge.addTokenAmount( tokenAmount);
                    return edge;
                };


        GraphMLImporter<LNVertex, LNEdge> importer =
                new GraphMLImporter<>(vertexProvider, edgeProvider);

        return importer;
    }


    public static void writeGraphML(SimpleDirectedWeightedGraph<LNVertex, LNEdge> graph, String file) {
        try {
            System.out.println("-- Exporting graph as GraphML");
            GraphExporter<LNVertex, LNEdge> exporter = createExporter();
            Writer writer = new StringWriter();
            exporter.exportGraph(graph, writer);
            String graph1AsGraphML = writer.toString();
            System.out.println(graph1AsGraphML);
            stringToXMLFile(graph1AsGraphML,file);
        } catch (ExportException e) {
            e.printStackTrace();
        }

    }

    public static SimpleDirectedWeightedGraph<LNVertex, LNEdge> readGraphML(String file) {
        SimpleDirectedWeightedGraph<LNVertex, LNEdge> graph = null;
        String graphString = xmlFileToString(file);
        try {
            System.out.println("-- Importing graph back from GraphML");
            graph = new SimpleDirectedWeightedGraph<>(LNEdge.class);
            GraphImporter<LNVertex, LNEdge> importer = createImporter();
            importer.importGraph(graph, new StringReader(graphString));
        } catch (ImportException e) {
            e.printStackTrace();
        }
        return graph;
    }

    public static void stringToXMLFile(String xmlSource, String file) {
        try {
            OutputStream os = new FileOutputStream(new File(file));
            PrintWriter p = new PrintWriter(os);
            p.println(xmlSource);
            p.flush();
            p.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String xmlFileToString(String file) {
        try {
            InputStream inputStream = new FileInputStream(new File(file));
            java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        NetworkTopologyAbstractFactory factory = NetworkTopologyAbstractFactory.getInstance(NetworkTopologyAbstractFactory.Type.FILE);
        NetworkTopology topology = factory.createTopology(2, 20);
        GraphIO.writeGraphML(topology.getNetworkGraph(), "./src/main/resources/graph.xml");

        SimpleDirectedWeightedGraph<LNVertex, LNEdge> graph2 = GraphIO.readGraphML("./src/main/resources/graph.xml");

        System.out.println(graph2);
        System.out.println(topology.getNetworkGraph());
        new NetworkGraphView(topology.getNetworkGraph());

        new NetworkGraphView(graph2);

    }


}