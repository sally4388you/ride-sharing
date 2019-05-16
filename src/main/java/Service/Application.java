package Service;

import Controller.RFP;
import Model.GraphBuilder;
import Model.Solution;

public class Application {

    public static void main(String[] args) throws Exception {

        // 1. generate new csv file
//        Trip.generateFromCSVFile();
//        Trip.generateSinglePath(47.447767,-122.243275,49.28381,-123.12, 8, 0, null, 0);

//        2. csv to RTrips
        GraphBuilder g = new GraphBuilder("src/main/resources/modified/RTripSingle.csv");
        RFP algorithm = new RFP(g.getSimpleDirectedGraph(), g.getVertices());
        Solution solution = algorithm.getSolution();
        solution.print(g.getVertices());

        // 3. algorithm FMCS with given data
//        FMCS test = new FMCS("src/main/resources/modified/RTrip3.csv");
//        test.getSolution();

        // 4. algorithm FMCS with examples in the paper
//        FMCS test = new FMCS("");
//        Map<Integer, Long> label = new HashMap<>();
//        Map<Integer, Integer> edge = new HashMap<>();
//        label.put(1, (long)1);
//        label.put(2, (long)1);
//        label.put(3, (long)1);
//        label.put(4, (long)1);
//        label.put(5, (long)1);
//        label.put(6, (long)1);
//        label.put(7, (long)1);
//
//        edge.put(4, 3);
//        edge.put(3, 2);
//        edge.put(2, 1);
//        edge.put(7, 6);
//        edge.put(6, 5);
//        edge.put(5, 1);
//        GraphComponent component = new GraphComponent(label, edge, 1);
//        test.algorithm(component);
    }
}
