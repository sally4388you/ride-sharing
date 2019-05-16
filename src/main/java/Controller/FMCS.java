//package Controller;
//
//import Model.*;
//import org.json.JSONArray;
//
//import java.awt.*;
//import java.awt.geom.Point2D;
//import java.io.FileNotFoundException;
//import java.util.*;
//
//class TripInfo {
//    long source;
//    int capacity;
//    double pathLength;
//
//    public TripInfo(int capacity, double pathLength) {
//        this.capacity = capacity;
//        this.pathLength = pathLength;
//    }
//
//    // test
//    public TripInfo(long source, int capacity, double pathLength) {
//        this.source = source;
//        this.capacity = capacity;
//        this.pathLength = pathLength;
//    }
//
//    public void setSource(long source) {
//        this.source = source;
//    }
//}
//
//public class FMCS extends Algorithm {
//
//    private Map<Integer, TripInfo> info = new HashMap<>();
//    private Set<Integer> sources = new HashSet<>();
//
//    public FMCS(String url) throws FileNotFoundException {
//        super(url);
//        // test
//        sources.add(4);
//        sources.add(7);
//        info.put(4, new TripInfo(4, 1, 5));
//        info.put(3, new TripInfo(4, 1, 4.5));
//        info.put(2, new TripInfo(4, 0, 4));
//        info.put(1, new TripInfo(4, 1, 2));
//        info.put(7, new TripInfo(7, 1, 5));
//        info.put(6, new TripInfo(7, 1, 4));
//        info.put(5, new TripInfo(7, 0, 2.5));
//    }
//
//    @Override
//    public Solution algorithm(GraphComponent component) {
//        Map<Integer, Long> label = new TreeMap<>(Comparator.reverseOrder());
//        label.putAll(component.getLabel());
//
//        Map<Point, Set<Solution>> X = new HashMap<>();
//
//        Iterator<Integer> it = label.keySet().iterator();
//        while (it.hasNext()) {
//            int i = it.next();
//
//            if (sources.contains(i)) {
//                Solution solution = new Solution();
//                solution.addDriverToS(i);
//
//                Set<Integer> descendantWithoutAtSigma = descendantWithoutAtSigma(i, component, solution);
//                int c = getC(i, descendantWithoutAtSigma); // - 1; // ??? -1
//                Set<Integer> sigma = new HashSet<>();
////                sigma.add(i);
//                sigma.addAll(N(i, c, descendantWithoutAtSigma));
//                solution.addPassengersToADriver(i, sigma);
//
//                Set<Solution> solutions = new HashSet<>();
//                solutions.add(solution);
//                X.put(new Point(i, i), solutions);
//            } else {
//                // X(i, vi) = X(i+1,v(i+1))
//                Set<Solution> solutions = new HashSet<>();
//
//                Point index = new Point(i, (int)info.get(i).source);
//                Point lastIndex = new Point(i + 1, (int) info.get(i + 1).source);
//                solutions.addAll(X.get(lastIndex));
//
//                // revise solution = revise solutions !!ATTENTION
//                for (Solution solution : X.get(lastIndex)) {
//                    Solution _solution = serve(i, solution, component);
//
//                    solutions.add(_solution);
//                }
//                X.put(index, solutions);
//
//                // whether S is a solution of Trip(i, vi)
//                Map<Double, Solution> dominateComputing = new TreeMap<>();
//                solutions = new HashSet<>();
//                solutions.addAll(X.get(index));
//                for (Solution solution : solutions) {
//                    if (solution.getSigmaAsSet().size() < index.getY() - index.getX()) {
//                        // not the solution of Trip(i, vi)
//                        X.remove(solution);
//                    } else {
//                        dominateComputing.put(dist(solution.getS()), solution);
//                    }
//                }
//
//                // whether is dominated by some S'
//                int lastSigmaSize = 0;
//                // distance small to big;
//                for (Solution solution : dominateComputing.values()) {
//                    if (lastSigmaSize == 0) {
//                        // should be smaller
//                        lastSigmaSize = solution.getSigmaAsSet().size();
//                        continue;
//                    }
//                    if (solution.getSigmaAsSet().size() > lastSigmaSize) {
//                        X.remove(solution);
//                    }
//                }
//
//                // if the point is a merge point
//                if (i - 1 == component.getSink()) {
//                    Set<Integer> parents = new TreeSet<>(Comparator.reverseOrder());
//                    for (int parent : component.getEdge().keySet()) {
//                        if (component.getEdge().get(parent) == i - 1) parents.add(parent);
//                    }
//
//                    Iterator<Integer> itParent = parents.iterator();
//                    int largerParent = itParent.next();
//                    while (itParent.hasNext()) {
//                        int smallerParent = itParent.next();
//                        X.replace(new Point(smallerParent, (int)info.get(i-1).source), Merge(
//                                X.get(new Point(smallerParent, (int)info.get(smallerParent).source)),
//                                X.get(new Point(largerParent, (int)info.get(i-1).source))
//                        ));
//                    }
//                }
//            }
//        }
//
//        // choose the minimum distance
//        double minimum = 0;
//        Solution finalSolution = new Solution();
//        for (Solution solution : X.get(new Point(1, label.size()))) {
//            double distance = calculateDistance(solution);
//            if (minimum == 0) {
//                minimum = distance;
//                finalSolution = solution;
//                continue;
//            }
//
//            if (minimum > distance) {
//                minimum = distance;
//                finalSolution = solution;
//            }
//        }
//
//        return finalSolution;
//    }
//
//    public void setGraph(Set<Trip> Trip) {
//        Map<Point2D.Double, Set<Trip>> G = new HashMap<>(); // same source
//        Map<Long, Long> edge = new HashMap<>();
//        Set<Long> vertex = new HashSet<>();
//        Map<Long, TripInfo> infoLongKey = new HashMap<>();
//        Set<Long> sourcesLongKey = new HashSet<>();
//
//        Map<Double, Point2D.Double> sortedG = new TreeMap<>(Collections.reverseOrder());
//
//        // partitioned into same group if having same source
//        for (Trip e : Trip) {
//            String sourceLat = e.getMapStartLat();
//            String sourceLng = e.getMapStartLng();
//
//            Point2D.Double point = new Point2D.Double(Double.valueOf(sourceLng), Double.valueOf(sourceLat));
//            Set<Trip> set = G.get(point);
//            if (set == null) {
//                set = new HashSet<>();
//            }
//            set.add(e);
//            G.put(point, set);
//            sortedG.putIfAbsent(e.getLength(), point);
//
//            TripInfo tripInfo = new TripInfo(e.getCapacity(), e.getLength());
//            infoLongKey.put(e.getId(), tripInfo);
//        }
//
//        // compute Hr
//        for(Point2D.Double point : sortedG.values()) {
//            Trip trip = G.get(point).iterator().next();
//            Long tripId = trip.getId();
//            if (vertex.contains(tripId)) continue;
//
//            vertex.add(tripId);
//            sourcesLongKey.add(tripId);
//            infoLongKey.get(tripId).setSource(tripId);
//            // from shorter to longer in terms of path length
//            // the former, the shorter
//            Set<Long> overlapTripLabelId = new LinkedHashSet<>();
//
//            JSONArray path = trip.getPath();
//
//            for (int i = path.length() - 2; i > 0; i --) {
//                JSONArray pathVertex = path.getJSONArray(i);
//                double lng = pathVertex.getDouble(0);
//                double lat = pathVertex.getDouble(1);
//                Set<Trip> isExist = G.get(new Point2D.Double(lng, lat));
//                if (isExist != null) {
//                    Trip overlapTrip = isExist.iterator().next();
//                    Long overlapTripId = overlapTrip.getId();
//
//                    if (!vertex.contains(overlapTripId)) {
//                        vertex.add(overlapTripId);
//                    }
//                    if (tripId == overlapTripId) continue;
//                    overlapTripLabelId.add(overlapTripId);
//                }
//            }
//
//            if (overlapTripLabelId.size() > 0) {
//                overlapTripLabelId.add(tripId);
//
//                Iterator<Long> it = overlapTripLabelId.iterator();
//                long sink = it.next(); // from the bottom; from index 0;
//                infoLongKey.get(sink).setSource(tripId);
//
//                while (it.hasNext()) {
//                    long source = it.next();
//                    edge.put(source, sink);
//                    sink = source;
//
//                    infoLongKey.get(source).setSource(tripId);
//                }
//            }
//        }
//
//        // add edge for trips having the same source
//        for (Set<Trip> trips : G.values()) {
//            if (trips.size() > 1) {
//                Iterator<Trip> it = trips.iterator();
//                long sink = it.next().getId();
//                long infoSource = infoLongKey.get(sink).source;
//                sourcesLongKey.remove(sink);
//
//                while (it.hasNext()) {
//                    long source = it.next().getId();
//                    edge.put(source, sink);
//                    sink = source;
//
//                    vertex.add(source);
//                    infoLongKey.get(source).setSource(infoSource);
//                }
//                sourcesLongKey.add(sink);
//            }
//        }
//
//        Graph2 T = new Graph2(vertex, edge);
//        setInfoNSource(T, infoLongKey, sourcesLongKey);
//
//        super.setGraph(T);
//    }
//
//    public void setSolution(Solution solution) {
//        // need to combine solutions
//        Solution oldSolution = super.getSolution();
//        oldSolution.getSigma().putAll(solution.getSigma());
//        oldSolution.getS().addAll(solution.getS());
//        super.setSolution(solution);
//    }
//
//    public Solution getSolution() {
//        Map<Point2D.Double,Set<Trip>> Trip = super.getTrips();
//
//        for (Set<Trip> set : Trip.values()) {
//            setGraph(set);
//            for (GraphComponent component : super.getGraph().getComponents()) {
//                Solution solution = algorithm(component);
//                setSolution(solution);
//            }
//        }
//
//        return super.getSolution();
//    }
//
//    private Set<Integer> N(int i, int c, Set<Integer> descendantWithoutAtSigma) {
//
//        Set<Integer> result = new HashSet<>();
//        Map<Double, Integer> order = new TreeMap<>();
//
//        for (int j : descendantWithoutAtSigma) {
//            order.put(info.get(i).pathLength - info.get(j).pathLength, j);
//        }
//
//        Iterator<Integer> it = order.values().iterator();
//        while (c -- > 0) {
//            if (it.hasNext())
//                result.add(it.next());
//        }
//        return result;
//    }
//
//    private int getC(int labelId, Set<Integer> descendantWithoutAtSigma) {
//
//        return Math.min(info.get(labelId).capacity + 1, descendantWithoutAtSigma.size());
//    }
//
//    private Set<Integer> descendantWithoutAtSigma(int labelId, GraphComponent component, Solution S) {
//        Set<Integer> result = new HashSet<>();
//
//        Set<Integer> descendant = component.descendant(labelId);
//        descendant.add(labelId);
//        Set<Integer> sigma = S.getSigmaAsSet();
//
//        for (int i : descendant) {
//            if (!sigma.contains(i)) result.add(i);
//        }
//        return result;
//    }
//
//    private void setInfoNSource(Graph2 T, Map<Long, TripInfo> infoLongKey, Set<Long> sourcesLongKey) {
//        for (GraphComponent component : T.getComponents()) {
//            HashMap<Long, Integer> labels = Graph2.reverse(component.getLabel());
//            for (long tripId : labels.keySet()) {
//                int label = labels.get(tripId);
//
//                TripInfo tripInfo = infoLongKey.get(tripId);
//                tripInfo.setSource(labels.get(tripInfo.source));
//                info.put(label, tripInfo);
//
//                if (sourcesLongKey.contains(tripId))
//                    sources.add(label);
//            }
//        }
//    }
//
//    private Solution serve(int i, Solution solution, GraphComponent component) {
//        Solution _solution = new Solution(solution);
//        _solution.addDriverToS(i);
//
//        Set<Integer> descendantWithoutAtSigma = descendantWithoutAtSigma(i, component, solution);
//        int c = getC(i, descendantWithoutAtSigma); // - 1;
//        Set<Integer> sigma = new HashSet<>();
//        sigma.addAll(N(i, c, descendantWithoutAtSigma));
//        _solution.addPassengersToADriver(i, sigma);
//
////        for (int k : solution.getSigmaAsSet()) {
////            if (!solution.getSigma().containsKey(k)) continue;
//
//        for (int k : solution.getS()) {
//            if (solution.getSigmaByDriverId(k).contains(i)) {
//                sigma = new HashSet<>(); // help!!!!!
//                sigma.addAll(_solution.getSigmaByDriverId(k));
//                sigma.remove(i);
//
//                descendantWithoutAtSigma = descendantWithoutAtSigma(k, component, _solution);
//                sigma.addAll(N(k, 1, descendantWithoutAtSigma));
//                _solution.addPassengersToADriver(k, sigma);
//                break;
//            }
//        }
//
//        return _solution;
//    }
//
//    @Override
//    public double evaluateDistance() {
//        Solution solution = super.getSolution();
//        return calculateDistance(solution);
//    }
//
//    private double calculateDistance(Solution solution) {
//        double distance = 0;
//        Set<Integer> drivers = solution.getS();
//        for (int driver : drivers) {
//            distance += info.get(driver).pathLength;
//        }
//
//        return distance;
//    }
//
//    public double dist(Set<Integer> S) {
//        double distance = 0;
//        for (int driver : S) {
//            distance += info.get(driver).pathLength;
//        }
//
//        return distance;
//    }
//
//    private Set<Solution> Merge(Set<Solution> X1, Set<Solution> X2) {
//        Set<Solution> solutions = new HashSet<>();
//        return solutions;
////        solution.setS(X1.getS());
////        solution.setS(X2.getS());
//    }
//}
