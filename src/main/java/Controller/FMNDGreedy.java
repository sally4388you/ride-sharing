package Controller;

import Model.*;
import org.apache.commons.lang3.ArrayUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import java.util.*;

/**
 * Find Minimum Number of Drivers
 *
 * author: Sally Qi
 * date: 2019/07/07
 */
public class FMNDGreedy extends Algorithm
{
    private SortedSet<Integer> sources;
    private Integer[] labels;

    /**
     * Set the graph and the trips for the algorithm
     *
     * @param graph  A simple directed graph
     * @param trips A set of trips
     */
    public FMNDGreedy(Graph<Integer, DefaultEdge> graph, ArrayList<Vertex> trips)
    {
        super(graph, trips);

        this.sources = new TreeSet<>();
        this.labels = this.setLabels();
    }

    /**
     * Contains main logic for the algorithm
     * private method for getting a solution regarding the given (G,R)
     */
    private void setSolution()
    {
        // LINE 1: S:={};
        Set<Integer> visited = new HashSet<>();
        // LINE 2: for every trip v ∈ At do
        for (int source : this.sources) {
            int x = source;
            Set<Integer> ancestors = getAncestors(x);
            // LINE 3: while (x ∈ At) or (all trips in Ax\{x} are marked) do
            while (x == source || areMarked(ancestors, visited)) {
                ancestors.retainAll(this.solution.getS());
                // LINE 4: let u be a trip in Ax\S with the largest nu;
                int u = ancestors.iterator().next();
                // LINE 5: if u != x, then
                if (u != x) {
                    int k = 0;
                    for (int driver : this.solution.getS()) {
                        // LINE 6: let k ∈ S s.t. u ∈ σ(k);
                        if (this.solution.getSigma().get(driver).contains(u)) {
                            k = driver;
                            break;
                        }
                    }
                    if (k != 0) {
                        // LINE 6: σ(k) := (σ(k)\{u}) U {x}
                        this.solution.removePassengerFromDriver(k, u);
                        this.solution.addPassengerToADriver(k, x);
                        // LINE 6: mark x;
                        visited.add(x);
                    }
                }
                Set<Integer> descendantWithoutAtSigma = descendants(u);
                // LINE 8: c := min{nu + 1, |Du\σ(S)|};
                int c = getC(u, descendantWithoutAtSigma);
                // LINE 8: S := S U {u};
                this.solution.addDriverToS(u);
                // LINE 9: σ(u) := N(u,c,S)
                solution.addPassengersToADriver(u, N(u, c, descendantWithoutAtSigma));
                // LINE 9: mark all trips in σ(u);
                visited.addAll(this.solution.getSigma().get(u));
                // LINE 9: if (all trips in Du are marked) then
                if (areMarked(descendantWithoutAtSigma, visited)) {
                    // LINE 10: break the while loop;
                    break;
                } else {
                    // LINE 12: let x be the unmarked trip in Du with the minimum dist(u->x) in T;
                    x = minDist(u);
                }

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
     * Procedure for assigning integer labels to trips in T.
     */
    private Integer[] setLabels()
    {
        // initialize ArrayList labels
        Integer[] labels = new Integer[this.trips.size() + 1];
        Arrays.fill(labels, -1);

        // LINE 1: i := l;
        int i = this.graph.vertexSet().size();

        // Find sink
        int sink = this.setSink();

        // LINE 1: let ST be a stack;
        Stack<Integer> ST = new Stack<>();
        // LINE 1: push the sink of T into ST;
        ST.push(sink);

        // LINE 2: mark every arc in T unvisited;
        Set<DefaultEdge> visited = new HashSet<>();
        // LINE 3: ST != empty do
        while (!ST.empty()) {
            // LINE 4: let u be the trip at the top of ST;
            Integer u = ST.peek();
            Iterator<DefaultEdge> it = this.graph.incomingEdgesOf(u).iterator();

            boolean findUnvisitedFlag = false;
            boolean isSourceFlag = false;
            if (it.hasNext()) {
                while (it.hasNext()) {
                    DefaultEdge e = it.next();
                    Integer v = this.graph.getEdgeSource(e);
                    // LINE 5: if there is an arc (v, u) in T unvisited then
                    if (!visited.contains(e)) {
                        // LINE 6: push v into ST;
                        ST.push(v);
                        // LINE 6: mark (v, u) visited;
                        visited.add(e);
                        findUnvisitedFlag = true;
                        break;
                    }
                }
            } else {
                isSourceFlag = true;
            }

            if (!findUnvisitedFlag) {
                if (isSourceFlag) sources.add(i);
                // LINE 8: remove u from ST;
                ST.remove(u);
                // LINE 8: assign u integer label i; i := i - 1;
                labels[i --] = u;
            }
        }

        return labels;
    }

    /**
     * @return sink
     */
    private int setSink()
    {
        // retrieve a vertex randomly
        int u = this.graph.vertexSet().iterator().next();

        // find its outgoing edges
        DefaultEdge edge;
        Set<DefaultEdge> e = this.graph.outgoingEdgesOf(u);

        // use DFS to find the deepest vertex
        while (!e.isEmpty()) {
            edge = e.iterator().next();
            // update vertex u
            u = this.graph.getEdgeTarget(edge);
            // update set of edges e
            e = this.graph.outgoingEdgesOf(u);
        }

        return u;
    }

    // TODO:: sort
    private Set<Integer> getAncestors(int x)
    {
        return null;
    }

    private boolean areMarked(Set<Integer> check, Set<Integer> visited)
    {
        return false;
    }

    private Set<Integer> descendants(int label)
    {
        Set<Integer> sigma;
        Set<Integer> sigma_origin = new HashSet<>();

        // IF TRUE: exclude trips that are in the solution
        sigma = this.solution.getAllPassengers();
        // change all label id to original id to get graph information
        for (int i : sigma) sigma_origin.add(this.labels[i]);

        Set<Integer> result = new HashSet<>();

        int u = this.labels[label];
        // find its outgoing edges
        DefaultEdge edge;
        Set<DefaultEdge> e = this.graph.outgoingEdgesOf(u);

        // check vertex i itself
        if (!sigma_origin.contains(u)) {
            result.add(ArrayUtils.indexOf(this.labels, u));
        }

        while (!e.isEmpty()) {
            edge = e.iterator().next();
            // update vertex u
            u = this.graph.getEdgeTarget(edge);
            // update set of edges e
            e = this.graph.outgoingEdgesOf(u);
            // if vertex u is not in the passenger set, add vertex u
            if (!sigma_origin.contains(u)) {
                result.add(ArrayUtils.indexOf(this.labels, u));
            }
        }

        return result;
    }

    private int getC(int label, Set<Integer> descendantWithoutAtSigma)
    {
        int n = this.trips.get(this.labels[label]).getTrip().getCapacity();

        return Math.min(n + 1, descendantWithoutAtSigma.size());
    }

    private Set<Integer> N(int i, int c, Set<Integer> descendantWithoutAtSigma)
    {
        Set<Integer> result = new HashSet<>();
        Map<Float, Integer> order = new TreeMap<>();

        for (int j : descendantWithoutAtSigma) {
            Float diff = this.trips.get(this.labels[i]).getTrip().getLength() - this.trips.get(this.labels[j]).getTrip().getLength();
            order.put(diff, j);
        }

        Iterator<Integer> it = order.values().iterator();
        while (it.hasNext() && c -- > 0) {
            result.add(it.next());
        }
        return result;
    }
}
