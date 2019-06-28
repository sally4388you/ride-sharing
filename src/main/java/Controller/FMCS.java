package Controller;

import Model.*;
import org.apache.commons.lang3.ArrayUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import java.util.*;

/**
 * Find Minimum Cost Solution
 *
 * author: Sally Qi
 * date: 2019/05/12
 */
public class FMCS extends Algorithm
{
    private SortedSet<Integer> sources;
    private Integer[] labels;
    private ArrayList<ArrayList<Set<Solution>>> X;

    private boolean allowDetour = false;

    /**
     * Set the graph and the trips for the algorithm
     *
     * @param graph  A simple directed graph
     * @param trips A set of trips
     */
    public FMCS(Graph<Integer, DefaultEdge> graph, ArrayList<Vertex> trips)
    {
        super(graph, trips);

        this.sources = new TreeSet<>(Collections.reverseOrder());
        this.labels = this.setLabels();
        this.X = new ArrayList<>(Collections.nCopies(this.trips.size() + 1, null));
    }

    /**
     * Contains main logic for the algorithm
     * private method for getting a solution regarding the given (G,R)
     */
    private void setSolution()
    {
        // LINE 1: for i := l to 1 do /* process every trip of T, l is a source */
        for (int i = this.labels.length - 1; i > 0; i --) {

            if (this.labels[i] == -1) continue;

            // LINE 2: if i is a source of T then /* process a source trip */
            if (this.sources.contains(i)) {
                Solution solution = new Solution();
                // LINE 3: S := {i};
                solution.addDriverToS(i);

                // Di\σ(S)
                Set<Integer> descendantWithoutAtSigma = descendants(i, solution, true);
                // LINE 3: c := min{ni + 1, |Di\σ(S)|};
                int c = getC(i, descendantWithoutAtSigma);
                // LINE 3: σ(i) := N(i, c, S);
                solution.addPassengersToADriver(i, N(i, c, descendantWithoutAtSigma));
                // LINE 3: X(i, i) := {(S, σ)};
                this.setX(i, i, solution);
            }
            // LINE 4: else /* process a non-source trip */
            else {
                int vi = this.getVi(i);
                int vi_add_1 = this.getVi(i + 1);
                // LINE 4 FIX: if i is a merge point then origin := v(i) else origin := v(i+1)
                int origin = this.isMergePoint(i) ? vi : vi_add_1;

                Set<Solution> solutions = this.X.get(i + 1).get(origin);
                // LINE 5: X(i, v(i)) = X(i+1, v(i + 1)); /* compute X(i, vi) */
                for (Solution s : solutions) this.setX(i, vi, s);

                // LINE 6: for every(S, σ) ∈ X(i + 1, v(i + 1)) do
                for (Solution s : solutions) {
                    // LINE 7: σ'(S') := Serve(i, S, σ);
                    Solution _solution = serve(i, s);

                    // LINE 7: X(i, v(i)) = X(i, v(i)) U {(S', σ')};
                    this.setX(i, vi, _solution);
                }

                Set<Solution> to_be_removed = new HashSet<>();
                // LINE 9: for every solution S in X(i, v(i)) do /* make X(i, v(i)) non-dominating */
                for (Solution s : this.X.get(i).get(vi)) {
                    // LINE 10: if S is not a solution of R(i, v(i)) then remove S from X(i, v(i));
                    if (!isSolution(s, i, vi)) {
                        to_be_removed.add(s);
                    }
                    // LINE 11: if S is dominated by some S' in X(i, v(i)) then remove S from X(i, v(i));
                    if (isDominated(s, this.X.get(i).get(vi))) {
                        to_be_removed.add(s);
                    }
                }
                for (Solution s : to_be_removed) {
                    this.X.get(i).get(vi).remove(s);
                }

                // LINE 13: if i - 1 is a merge point then /* merge solutions */
                if (i > 1 && this.isMergePoint(i - 1)) {
                    // LINE 14: let i1, ..., ir be the parents of i - 1 with ia < ib for a < b;
                    int[] parents = this.getParents(i -1);
                    int vi_1 = getVi(i-1);

                    // LINE 15: for a := r to 2
                    for (int a = parents.length - 1; a > 1;  a--) {
                        // X(i(a-1), v(i(a-1)))
                        Set<Solution> s1 = this.X.get(parents[a-1]).get(getVi(parents[a-1]));
                        // X(i(a), v(i-1))
                        Set<Solution> s2 = this.X.get(parents[a]).get(vi_1);
                        int comparator = vi_1 - parents[a-1] + 1 + descendants(i-1, null, false).size();

                        // LINE 15: do X(i(a-1), v(i-1)) := Merge(X(i(a-1), v(i(a-1))), X(i(a), v(i-1)));
                        Set<Solution> mergedSolution = this.merge(s1, s2, comparator);
                        for (Solution s: mergedSolution) {
                            this.setX(parents[a - 1], vi_1, s);
                        }
                    }
                }
            }
        }

        Map<Float, Solution> min_distance = new TreeMap<>();
        for (Solution s: this.X.get(1).get(getVi(1))) {
            min_distance.put(this.dist(s), s);
        }

        // LINE 19: Let (S, σ) be a solution in X(1, l) with the minimum dist(S);
        Solution solution_tmp = min_distance.values().iterator().next();
        this.translateFromLabelToRealTrip(solution_tmp);
    }

    /**
     * Return parents with an increasing order
     * position starts from 1, which means parents[0] stored a junk number
     *
     * @param i label
     * @return a set of parents of i
     */
    private int[] getParents(int i)
    {
        int vertex = this.labels[i];
        Set<DefaultEdge> edges = this.graph.incomingEdgesOf(vertex);

        int[] parents = new int[edges.size() + 1];
        parents[0] = -1;
        // position starts from 1
        int pos = 1;

        for (DefaultEdge e: edges) {
            parents[pos ++] = this.graph.getEdgeSource(e);
        }

        // sort the array
        Arrays.sort(parents);

        return parents;
    }

    /**
     * Check if the vertex is a merge point
     * @param i label
     * @return boolean
     */
    private boolean isMergePoint(int i)
    {
        int vertex = this.labels[i];
        Set<DefaultEdge> edges = this.graph.incomingEdgesOf(vertex);

        return edges.size() > 1;
    }

    /**
     * Check if the solution is dominated by other solutions in the set
     * @param solution  solution to be checked
     * @param solutions a set of solutions
     * @return boolean
     */
    private boolean isDominated(Solution solution, Set<Solution> solutions)
    {
        float distance = this.dist(solution);

        for (Solution s : solutions) {
            if (s == solution) continue;

            // if |σ(S)| >= |σ(S')| and dist(S) <= dist(S')
            if (s.getAllPassengers().size() >= solution.getAllPassengers().size() && this.dist(s) <= distance) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the solution is an actually solution for R(start, end)
     * @param solution solution to be checked
     * @param start start label in the trip set
     * @param end end label in the trip set
     * @return boolean
     */
    private boolean isSolution(Solution solution, int start, int end)
    {
        for (int i = start; i <= end; i ++) {
            if (!solution.getAllPassengers().contains(i)) return false;
        }
        return true;
    }

    /**
     * Assign a solution to the set of solutions X
     * @param i X(i, j) is to be set
     * @param j X(i, j) is to be set
     * @param solution a solution that is to be assigned to X(i, j)
     */
    private void setX(int i, int j, Solution solution)
    {
        if (this.X.get(i) == null) {
            this.X.set(i, new ArrayList<>(Collections.nCopies(this.trips.size() + 1, null)));
        }

        if (this.X.get(i).get(j) == null) {
            this.X.get(i).set(j, new HashSet<>());
        }

        this.X.get(i).get(j).add(solution);
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
     * Set the parameter allowDetour
     * @param allowDetour boolean
     */
    public void setAllowDetour(boolean allowDetour)
    {
        this.allowDetour = allowDetour;
    }

    /**
     * @param i label id
     * @param c minimum number
     * @param descendantWithoutAtSigma Di
     * @return N(i, c, S)
     */
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

    /**
     * c := min{ni + 1, |Di\σ(S)|}
     *
     * @param label label
     * @param descendantWithoutAtSigma Di\σ(S
     * @return int
     */
    private int getC(int label, Set<Integer> descendantWithoutAtSigma)
    {
        int n = this.trips.get(this.labels[label]).getTrip().getCapacity();

        return Math.min(n + 1, descendantWithoutAtSigma.size());
    }

    /**
     * Retrieve a set of descendants of a label
     * A set of descendants include itself
     *
     * @param label new label id
     * @param solution a instance of solution
     * @return a set of label id
     */
    private Set<Integer> descendants(int label, Solution solution, boolean withoutSigma)
    {
        Set<Integer> sigma;
        Set<Integer> sigma_origin = new HashSet<>();

        // IF TRUE: exclude trips that are in the solution
        // ELSE: sigma_origin is empty
        if (withoutSigma) {
            sigma = solution.getAllPassengers();
            // change all label id to original id to get graph information
            for (int i : sigma) sigma_origin.add(this.labels[i]);
        }

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

    /**
     * Get vi
     * vi is the ancestor of a label with the largest number
     *
     * @param child_label label
     * @return int
     */
    private int getVi(int child_label)
    {
        int child = this.labels[child_label];

        if (this.sources.contains(child_label)) return child_label;

        for (int source : this.sources) {
            // find its outgoing edges
            int u = this.labels[source];
            DefaultEdge edge;
            Set<DefaultEdge> e = this.graph.outgoingEdgesOf(u);

            // use DFS to find the deepest vertex
            while (!e.isEmpty()) {
                edge = e.iterator().next();
                // update vertex u
                u = this.graph.getEdgeTarget(edge);
                if (u == child) {
                    return source;
                }
                // update set of edges e
                e = this.graph.outgoingEdgesOf(u);
            }
        }

        return 0;
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

    /**
     * Procedure Serve(i, S, σ)
     *
     * @param i label
     * @param solution solution that contains S and σ
     * @return Solution
     */
    private Solution serve(int i, Solution solution)
    {
        Solution _solution = new Solution();
        _solution.getS().addAll(solution.getS());
        // LINE 7 from main logic: S' := S U {i};
        _solution.addDriverToS(i);

        // LINE 2: σ'(j) := σ(j) for every j ∈ S;
        for (int j: solution.getS()) _solution.addPassengersToADriver(j, solution.getSigma().get(j));

        // LINE 3: if i ∉ σ(S) then
        if (!solution.getAllPassengers().contains(i))
        {
            // Di \ σ(S)
            Set<Integer> descendantWithoutAtSigma = descendants(i, solution, true);
            // LINE 4: c := min{ni + 1, |Di \ σ(S)|};
            int c = getC(i, descendantWithoutAtSigma);
            // LINE 4: σ'(i) := N(i, c, S);
            _solution.addPassengersToADriver(i, N(i, c, descendantWithoutAtSigma));
        }
        else
        {
            // Di \ σ(S)
            Set<Integer> descendantWithoutAtSigma = descendants(i, solution, true);
            // ni
            int n = this.trips.get(this.labels[i]).getTrip().getCapacity();
            // LINE 6: c := min{ni, |Di \ σ(S)|};
            int c = Math.min(n, descendantWithoutAtSigma.size());
            // LINE 6: σ'(i) := N(i, c, S);
            _solution.addPassengersToADriver(i, N(i, c, descendantWithoutAtSigma));
            // LINE 6: σ'(i) := N(i, c, S) U {i};
            _solution.addPassengerToADriver(i, i);
            // LINE 7: let k ∈ S s.t. i ∈ σ(k);
            for (int k : solution.getS()) {
                if (solution.getSigma().get(k).contains(i)) {
                    // LINE 7: σ'(k) := σ'(k)\{i};
                    _solution.removePassengerFromDriver(k, i);

                    // Dk \ σ(S')
                    descendantWithoutAtSigma = descendants(k, _solution, true);
                    // LINE 7: σ'(k) := σ'(k) U N(k, 1, S');
                    _solution.addPassengersToADriver(k, N(k, 1, descendantWithoutAtSigma));
                    break;
                }
            }
        }

        return _solution;
    }

    @Override
    public double evaluateDistance()
    {
        return this.dist(this.solution);
    }

    /**
     * Calculate the total distance that drivers in a solution need to travel
     *
     * @param solution solution to be calculated
     * @return distance
     */
    private float dist(Solution solution)
    {
        float distance = 0;

        if (this.allowDetour) {
            for (int driver : solution.getS()) {
                int _passenger = -1;
                for (int passenger : solution.getSigma().get(driver)) {
                    distance += this.trips.get(this.labels[driver]).getTrip().getLength() - this.trips.get(this.labels[passenger]).getTrip().getLength();
                    _passenger = passenger;
                }

                if (_passenger != -1) distance += this.trips.get(this.labels[_passenger]).getTrip().getLength();
            }
        } else {
            for (int driver : solution.getS()) {
                distance += this.trips.get(this.labels[driver]).getTrip().getLength();
            }
        }

        return distance;
    }

    /**
     * Procedure Merge(X(i(a-1), v(i(a-1))), X(i(a), v(i-1)));
     * @param X1 X(i(a-1), v(i(a-1)))
     * @param X2 X(i(a), v(i-1))
     * @param comparator |R(i(a-1), v(i-1))| + |Di-1|
     * @return a set of solutions
     */
    private Set<Solution> merge(Set<Solution> X1, Set<Solution> X2, int comparator) {
        Set<Solution> solutions = new HashSet<>();
        Map<Solution, Float> distance = new HashMap<>();
        Map<Solution, Integer> numberOfPassengers = new HashMap<>();

        for (Solution s1: X1) {
            for (Solution s2: X2) {
                Solution solution = new Solution();

                // LINE 1: S'' = S U S' in for S ∈ X(i(a-1), v(i(a-1))) and S' ∈ X(i(a), v(i-1));
                solution.getS().addAll(s1.getS());
                solution.getS().addAll(s2.getS());

                // LINE 2: dist(S'') = dist(S) + dist(S');
                distance.put(solution, this.dist(s1) + this.dist(s2));
                // LINE 3: set |σ''(S'')| to min{|R(i(a-1), v(i-1))| + |Di-1|, |σ(S)|+|σ(S')|};
                numberOfPassengers.put(solution, Math.min(comparator, s1.getAllPassengers().size() + s2.getAllPassengers().size()));

                for (int j : s2.getS()) {
                    // LINE 6: σ''(j) := σ'(j) for j ∈ S';
                    solution.addPassengersToADriver(j, s2.getSigma().get(j));
                }
                for (int j : s1.getS()) {
                    Set<Integer> passengers = new HashSet<>(s1.getSigma().get(j));
                    // σ(j) \ σ'(S')
                    passengers.removeAll(s2.getAllPassengers());

                    // LINE 6: σ''(j) := σ(j) \ σ'(S') for j ∈ S;
                    solution.addPassengersToADriver(j, passengers);
                }
                // LINE 7: for every j ∈ S
                for (int j : s1.getS()) {
                    //  σ(j)
                    Set<Integer> intersection = new HashSet<>(s1.getSigma().get(j));
                    //  σ(j) ∩ σ'(S')
                    intersection.retainAll(s2.getAllPassengers());

                    // Dj \ σ(S'')
                    Set<Integer> descendantWithoutAtSigma = descendants(j, s2, true);
                    // LINE 7: cj := |σ(j) ∩ σ'(S')|;
                    int c = intersection.size();
                    // LINE 7: σ''(j) := σ''(j) U N(j, cj, S'');
                    solution.addPassengersToADriver(j, N(j, c, descendantWithoutAtSigma));
                }

                solutions.add(solution);
            }
        }

        // LINE 4: remove S'' from X(i(a-1), v(i-1)) if S'' is dominated;
        Set<Solution> to_be_removed = new HashSet<>();
        for (Solution this_solution: solutions) {
            for (Solution s : solutions) {
                if (this_solution == s) continue;

                // if |σ(S)| >= |σ(S')| and dist(S) <= dist(S')q
                if (numberOfPassengers.get(s) >= numberOfPassengers.get(this_solution) && distance.get(s) <= distance.get(this_solution)) {
                    to_be_removed.add(this_solution);
                }
            }
        }
        for (Solution s : to_be_removed) {
            solutions.remove(s);
        }

        return solutions;
    }

    private void translateFromLabelToRealTrip(Solution solution)
    {
        for (int i: solution.getS()) {
            this.solution.addDriverToS(this.labels[i]);
        }

        for (int i: solution.getSigma().keySet()) {
            Set<Integer> passengers = new HashSet<>();
            for (int j: solution.getSigma().get(i)) {
                passengers.add(this.labels[j]);
            }
            this.solution.addPassengersToADriver(this.labels[i], passengers);
        }
    }
}
