package Controller;

import Model.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.DepthFirstIterator;

import java.awt.*;
import java.util.*;

/**
 * Ridesharing-For-Path
 *
 * author: Sally Qi
 * date: 2019/05/12
 */
public class RFP extends Algorithm
{
    private Set<Integer> SF;
    private Map<Point, Integer> gap;
    private Integer[] labels;

    /**
     * Set the graph and the trips for the algorithm
     *
     * @param graph A simple directed graph
     * @param trips A set of trips
     */
    public RFP(Graph<Integer, DefaultEdge> graph, ArrayList<Vertex> trips)
    {
        super(graph, trips);
        this.gap = new HashMap<>();
        this.SF = new HashSet<>();
        this.labels = this.setLabels();
    }

    /**
     * Contains main logic for the algorithm
     * private method for getting a solution regarding the given (G,R)
     */
    private void setSolution()
    {
        // initialize necessary variables
        int k;

        // find the first vertex for DFS traversal
        int start = this.graph.vertexSet().iterator().next();
        Iterator<Integer> iterator = new DepthFirstIterator<>(this.graph, start);

        // LINE 2: for i = 2 to l do
        // the graph contains a destination vertex which may not be in trips
        for (int i = 1; i < this.labels.length; i ++) {
            if (this.labels[i] == -1) continue;

            // LINE 1: S := {1}; // LINE 3: S:= S U {i};
            solution.addDriverToS(i);
            // LINE 1: σ(1) := {1}; // LINE 3: w(i) := {i};
            solution.addPassengerToADriver(i, i);
            if (i == 1) continue;

            // LINE 3: compute free(i) and SF;
            // start from the second vertex of trips
            computeSF();
            // LINE 3: k := Find-Target(i);
            k = findTarget(i);
            // LINE 4: while k!= 0 do /* serve σ(k) by drivers in SF and removek from S */
            while (k != -1) {
                for (int j : SF) {
                    // LINE 5: for each j ∈ SF, k < j < i do
                    if (k < j && j <= i) {
                        // LINE 6: move free(j) passengers from σ(k) to σ(j) and update free(j);
                        int free = free(j);
                        Set<Integer> setToBeMoved = new HashSet<>(solution.getSigma().get(k));
                        for (int move : setToBeMoved) {
                            if (free-- > 0) {
                                // move free(j) passengers to σ(j)
                                solution.addPassengerToADriver(j, move);
                                // move free(j) passengers from σ(k)
                                solution.removePassengerFromDriver(k, move);
                            }
                        }
                    }
                }
                Set<Integer> setToBeMoved = new HashSet<>(solution.getSigma().get(k));
                // LINE 8: move the remaining passengers and driver k of σ(k) to σ(j) and update free(i);
                for (int move : setToBeMoved) {
                    if (move == k) continue;
                    // move free(j) passengers to σ(i)
                    solution.addPassengerToADriver(i, move);
                    // move free(j) passengers from σ(k)
                    solution.removePassengerFromDriver(k, move);
                }
                // LINE 9: remove k from S and update SF;
                solution.removeDriverFromS(k);
                computeSF();
                // LINE 10: if free(i) >= 1 and |S| >= 2 then k := Find-Target(i);
                k = (free(i) >= 1 && solution.lengthOfS() >= 2) ? findTarget(i) : -1;
            }
        }
        this.translateFromLabelToRealTrip();
    }

    /**
     * Get solution from parent if the solution has been set
     * Otherwise compute solution
     *
     * @return Solution
     */
    public Solution getSolution()
    {
        if (this.solution.lengthOfS() == 0) {
            // call the main logic of the current algorithm
            this.setSolution();
        }

        return this.solution;
    }

    /**
     * Implement free(i) in the algorithm
     * free(i) = n(i) - |σ(i)| + 1
     *
     * @param label_id i
     * @return the number of additional trips i can serve
     */
    private int free(int label_id)
    {
        int id = this.labels[label_id];
        int capacity = this.trips.get(id).getTrip().getCapacity();
        return capacity - solution.lengthOfSigma(label_id) + 1;
    }

    /**
     * Implement computeSF() in the algorithm
     * SF = {i|i ∈ S and free(i) > 0}
     * SF is the set of drivers in S who can serve additional trips
     */
    private void computeSF()
    {
        SF = new HashSet<>();
        for (int id : solution.getS()) {
            if (free(id) > 0) SF.add(id);
        }
    }

    /**
     * Implement Find-Target(i) in the algorithm
     *
     * @param i i
     * @return int
     */
    private int findTarget(int i)
    {
        int free = 0;
        int k = -1;
        int min = -1;
        // LINE 1: for each j ∈ S \ {i} do
        for (int j : solution.getS()) {
            if (j == i) continue;
            //a ∈ SF, j < a < i
            for (int a : SF) {
                // accumulate free(a)
                if (a > j && a < i) free += free(a);
            }
            Point index = new Point(i, j);
            // LINE 1: gap(i,j) := |σ(j)|−∑free(a);
            gap.put(index, solution.lengthOfSigma(j) - free);

            // LINE 2: Let k = min{gap(i,j)};
            if (min == -1 || gap.get(index) < min) {
                min = gap.get(index);
                k = j;
            }
        }

        // LINE 3: if free(i) >= gap(i,k) then return k else return 0;
        return free(i) >= min ? k : -1;
    }

    /**
     * Procedure for assigning integer labels to trips in T.
     */
    private Integer[] setLabels()
    {
        // retrieve a vertex randomly
        Integer[] labels = new Integer[this.trips.size() + 1];
        Arrays.fill(labels, -1);

        int i = this.graph.vertexSet().size();

        int u = this.graph.vertexSet().iterator().next();
        labels[i --] = u;

        // find its outgoing edges
        DefaultEdge edge;
        Set<DefaultEdge> e = this.graph.outgoingEdgesOf(u);

        // use DFS to find the deepest vertex
        while (!e.isEmpty()) {
            edge = e.iterator().next();
            // update vertex u
            u = this.graph.getEdgeTarget(edge);
            labels[i --] = u;
            // update set of edges e
            e = this.graph.outgoingEdgesOf(u);
        }

        return labels;
    }

    /**
     * Translate from labels to real trips for a specific solution
     */
    private void translateFromLabelToRealTrip()
    {
        Solution translatedSolution = new Solution();

        for (int i: this.solution.getS()) {
            translatedSolution.addDriverToS(this.labels[i]);
        }

        for (int i: this.solution.getSigma().keySet()) {
            Set<Integer> passengers = new HashSet<>();
            for (int j: this.solution.getSigma().get(i)) {
                passengers.add(this.labels[j]);
            }
            translatedSolution.addPassengersToADriver(this.labels[i], passengers);
        }
        this.solution = translatedSolution;
    }
}
