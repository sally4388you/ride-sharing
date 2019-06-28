package Model;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;

/**
 * Abstract class Algorithm: every algorithm needs to have these three functionality
 *
 * author: Sally Qi
 * date: 2019/05/12
 */
public abstract class Algorithm implements BaseAlgorithm
{
    protected Graph<Integer, DefaultEdge> graph; // INPUT: a road network G
    protected ArrayList<Vertex> trips; // INPUT: a set of trips R
    protected Solution solution; // OUTPUT: a solution (S, σ)

    public Algorithm(Graph<Integer, DefaultEdge> graph, ArrayList<Vertex> trips)
    {
        this.graph = graph;
        this.trips = trips;
        this.solution = new Solution();
    }

    /**
     * Evaluate performance of the algorithm with number of drivers
     *
     * @return int
     */
    public int evaluateNumberOfDrivers() {
        return solution.getS().size();
    }

    /**
     * Evaluate performance of the algorithm with distance
     *
     * @return double
     */
    public double evaluateDistance() {
        return 0;
    }
}
