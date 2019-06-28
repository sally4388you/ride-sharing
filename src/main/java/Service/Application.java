package Service;

import Controller.FMCS;
import Model.GraphBuilder;
import Model.Solution;
import Model.Trip;
import Model.Vertex;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.ArrayList;
import java.util.Collections;

public class Application {

    public static void main(String[] args) throws Exception {

        // 1. generate new csv file
//        Trip.generateFromCSVFile();
//        Trip.generateSinglePath(47.447767,-122.243275,49.28381,-123.12, 8, 0, null, 0);

        // 3. algorithm FMCS with given data
//        GraphBuilder g = new GraphBuilder("src/main/resources/modified/RTripSingle.csv");
//        FMCS algorithm = new FMCS(g.getSimpleDirectedGraph(), g.getVertices());
//        Solution solution = algorithm.getSolution();
//        solution.print(g.getVertices());

        // 4. algorithm FMCS with examples in the paper
        Graph<Integer, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);

        ArrayList<Vertex> vertices = new ArrayList<>(Collections.nCopies(8, null));
        vertices.set(0, null);
        vertices.set(1, new Vertex(1, new Trip().setTestData(1, 1, 2)));
        vertices.set(2, new Vertex(2, new Trip().setTestData(2, 0, 4)));
        vertices.set(3, new Vertex(3, new Trip().setTestData(3, 1, (float) 4.5)));
        vertices.set(4, new Vertex(4, new Trip().setTestData(4, 1, 5)));
        vertices.set(5, new Vertex(5, new Trip().setTestData(5, 0, (float) 2.5)));
        vertices.set(6, new Vertex(6, new Trip().setTestData(6, 1, 4)));
        vertices.set(7, new Vertex(7, new Trip().setTestData(7, 1, 5)));

        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);
        graph.addVertex(6);
        graph.addVertex(7);

        graph.addEdge(4, 3);
        graph.addEdge(3, 2);
        graph.addEdge(2, 1);
        graph.addEdge(7, 6);
        graph.addEdge(6, 5);
        graph.addEdge(5, 1);


        FMCS algorithm = new FMCS(graph, vertices);
        Solution solution = algorithm.getSolution();
        solution.printSimple();
    }
}
