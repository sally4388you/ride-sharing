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
    }

    /**
     * Contains main logic for the algorithm
     * private method for getting a solution regarding the given (G,R)
     */
    private void setSolution()
    {
        // initialize necessary variables
        int count = 0;
        int k;

        // find the first vertex for DFS traversal
        int start = this.graph.vertexSet().iterator().next();
        Iterator<Integer> iterator = new DepthFirstIterator<>(this.graph, start);

        // LINE 2: for i = 2 to l do
        while (iterator.hasNext()) {
            int i = iterator.next();

            // the graph contains a destination vertex which may not be in trips
            if (i >= this.trips.size()) continue;

            // LINE 1: S := {1}; // LINE 3: S:= S U {i};
            solution.addDriverToS(i);
            // LINE 1: σ(1) := {1}; // LINE 3: w(i) := {i};
            solution.addPassengerToADriver(i, i);
            if (count ++ == 0) continue;

            // LINE 3: compute free(i) and SF;
            // start from the second vertex of trips
            computeSF();
            // LINE 3: k := Find-Target(i);
            k = findTarget(i);
            // LINE 4: while k!= 0 do /* serve σ(k) by drivers in SF and removek from S */
            while (k != 0) {
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
                k = (free(i) >= 1 && solution.lengthOfS() >= 2) ? findTarget(i) : 0;
            }
        }
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
     * @param id i
     * @return the number of additional trips i can serve
     */
    private int free(int id)
    {
        int capacity = this.trips.get(id).getTrip().getCapacity();
        return capacity - solution.lengthOfSigma(id) + 1;
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
        int k = 0;
        int min = 10;
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
            if (gap.get(index) < min) {
                min = gap.get(index);
                k = j;
            }
        }

        // LINE 3: if free(i) >= gap(i,k) then return k else return 0;
        return free(i) >= min ? k : 0;
    }
}
