package Service;

import Controller.FMCS;
import Controller.FMNDGreedy;
import Controller.RFP;
import Model.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

public class TestCases
{
    public static void main(String[] args) throws Exception
    {
        testRFP();
//        testFMCS();
//        testFMNDGreedy();
//        testFMCSWithFile();
//        testMiscellaneous();
    }

    /**
     * Test RFP algorithm with a simple path
     * @throws FileNotFoundException throw error if data is missing
     */
    static private void testRFP() throws FileNotFoundException
    {
        GraphBuilder g = new GraphBuilder("src/main/resources/modified/RTripSingle.csv");
        RFP algorithm = new RFP(g.getSimpleDirectedGraph(), g.getVertices());
        Solution solution = algorithm.getSolution();
        solution.print(g.getVertices());
    }

    /**
     * Test dynamic programming algorithm with the example in the paper
     */
    static private void testFMCS()
    {
        Graph<Integer, DefaultEdge> graph = exampleFromPaper();
        ArrayList<Vertex> vertices = vertexExampleFromPaper();
        FMCS algorithm = new FMCS(graph, vertices);
        Solution solution = algorithm.getSolution();
        solution.printSimple();
    }

    /**
     * Test the greedy algorithm with the example in the paper
     */
    static private void testFMNDGreedy()
    {
        Graph<Integer, DefaultEdge> graph = exampleFromPaper();
        ArrayList<Vertex> vertices = vertexExampleFromPaper();
        FMNDGreedy algorithm = new FMNDGreedy(graph, vertices);
        Solution solution = algorithm.getSolution();
        solution.printSimple();
    }

    /**
     * Test dynamic programming algoirthm with a simple path
     * @throws FileNotFoundException throw error if data is missing
     */
    static private void testFMCSWithFile() throws FileNotFoundException
    {
        GraphBuilder g = new GraphBuilder("src/main/resources/modified/RTripSingle.csv");
        FMCS algorithm = new FMCS(g.getSimpleDirectedGraph(), g.getVertices());
        Solution solution = algorithm.getSolution();
        solution.print(g.getVertices());
    }

    /**
     * Build a graph for the example in the paper
     * @return graph
     */
    static private Graph<Integer, DefaultEdge> exampleFromPaper()
    {
        Graph<Integer, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);

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

        return graph;
    }

    /**
     * Build vertices from the example in the paper
     * @return a list of vertices
     */
    static private ArrayList<Vertex> vertexExampleFromPaper()
    {
        ArrayList<Vertex> vertices = new ArrayList<>(Collections.nCopies(8, null));
        vertices.set(0, null);
        vertices.set(1, new Vertex(1, new Trip().setTestData(1, 1, 2)));
        vertices.set(2, new Vertex(2, new Trip().setTestData(2, 0, 4)));
        vertices.set(3, new Vertex(3, new Trip().setTestData(3, 1, (float) 4.5)));
        vertices.set(4, new Vertex(4, new Trip().setTestData(4, 1, 5)));
        vertices.set(5, new Vertex(5, new Trip().setTestData(5, 0, (float) 2.5)));
        vertices.set(6, new Vertex(6, new Trip().setTestData(6, 1, 4)));
        vertices.set(7, new Vertex(7, new Trip().setTestData(7, 1, 5)));

        return vertices;
    }

    /**
     * Test dataset generating functionality
     * @throws Exception throw error if writing files fails
     */
    static private void testMiscellaneous() throws Exception
    {
        // Test generate a standard csv file from a given dataset
//        Miscellaneous.generateFromCSVFile();
        // Test generate a standard csv file given coordinates of a source and a destination
        Miscellaneous.generateSinglePath(47.447767,-122.243275,49.28381,-123.12, 8, 0, null, 0);
    }
}
