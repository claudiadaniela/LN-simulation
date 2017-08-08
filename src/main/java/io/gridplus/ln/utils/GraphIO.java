package io.gridplus.ln.utils;

import java.io.*;
import java.util.*;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.NetworkTopology;
import io.gridplus.ln.simulator.NetworkTopologyGenerator;
import io.gridplus.ln.view.NetworkGraphView;
import org.jgrapht.io.ComponentAttributeProvider;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;
import org.jgrapht.io.GraphImporter;
import org.jgrapht.io.GraphMLExporter;
import org.jgrapht.io.GraphMLExporter.*;
import org.jgrapht.graph.*;
import org.jgrapht.io.GraphMLImporter;
import org.jgrapht.io.ImportException;
import org.jgrapht.io.IntegerComponentNameProvider;
import org.jgrapht.io.VertexProvider;

public final class GraphIO {
    private static final String FILE = "/src/main/resources/graph.xml";

    private static GraphExporter<LNVertex, LNEdge> createExporter() {
        GraphMLExporter<LNVertex, LNEdge> exporter =
                new GraphMLExporter<>((v) -> v.getId() + "", null, new IntegerComponentNameProvider<>(), null);

        exporter.setExportEdgeWeights(true);
        exporter.registerAttribute("fee", AttributeCategory.NODE, AttributeType.DOUBLE);
        exporter.registerAttribute("networkStatus", AttributeCategory.NODE, AttributeType.DOUBLE);
        exporter.registerAttribute("tokenAmount", AttributeCategory.EDGE, AttributeType.INT);


        ComponentAttributeProvider<LNVertex> vertexAttributeProvider =
                v -> {
                    Map<String, String> m = new HashMap<>();
                    if (v.getFee() != 0) {
                        m.put("fee", v.getFee() + "");
                        m.put("networkStatus", v.networkStatus.getHealthScore() + "");
                    }
                    return m;
                };
        exporter.setVertexAttributeProvider(vertexAttributeProvider);


        ComponentAttributeProvider<LNEdge> edgeAttributeProvider =
                e -> {
                    Map<String, String> m = new HashMap<>();
                    m.put("tokenAmount", e.tokenAmount + "");
                    return m;
                };
        exporter.setEdgeAttributeProvider(edgeAttributeProvider);

        return exporter;
    }


    private static GraphImporter<LNVertex, LNEdge> createImporter() {

        VertexProvider<LNVertex> vertexProvider = (id, attributes) -> {
            int idValue = Integer.parseInt(id);
            LNVertex v = new LNVertex(idValue);
            if (attributes.get("fee") != null) {
                double feeValue = Double.parseDouble(attributes.get("fee"));
                v.setFee(feeValue);
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
                    edge.tokenAmount = tokenAmount;
                    return edge;
                };


        GraphMLImporter<LNVertex, LNEdge> importer =
                new GraphMLImporter<>(vertexProvider, edgeProvider);

        return importer;
    }


    public static void writeGraphML(SimpleDirectedWeightedGraph<LNVertex, LNEdge> graph) {
        try {
            System.out.println("-- Exporting graph as GraphML");
            GraphExporter<LNVertex, LNEdge> exporter = createExporter();
            Writer writer = new StringWriter();
            exporter.exportGraph(graph, writer);
            String graph1AsGraphML = writer.toString();
            System.out.println(graph1AsGraphML);
            stringToXMLFile(graph1AsGraphML);
        } catch (ExportException e) {
            e.printStackTrace();
        }

    }

    public static SimpleDirectedWeightedGraph<LNVertex, LNEdge> readGraphML() {
        SimpleDirectedWeightedGraph<LNVertex, LNEdge> graph = null;
        String graphString = xmlFileToString();
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

    public static void stringToXMLFile(String xmlSource) {
        try {
            OutputStream os = new FileOutputStream(new File("./src/main/resources/graph.xml"));
            PrintWriter p = new PrintWriter(os);
            p.println(xmlSource);
            p.flush();
            p.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String xmlFileToString() {
        try {
            InputStream inputStream = new FileInputStream(new File("./src/main/resources/graph.xml"));
            java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        NetworkTopology topology = NetworkTopologyGenerator.generateRandomTopology(2, 20, 100);
        GraphIO.writeGraphML(topology.getNetworkGraph());

        SimpleDirectedWeightedGraph<LNVertex, LNEdge> graph2 = GraphIO.readGraphML();

        System.out.println(graph2);
        System.out.println(topology.getNetworkGraph());
        new NetworkGraphView(topology.getNetworkGraph());

        new NetworkGraphView(graph2);

    }


}