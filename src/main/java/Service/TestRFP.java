package Service;

import Controller.FMCS;
import Controller.RFP;
import Model.GraphBuilder;
import Model.Solution;

public class TestRFP
{
    public static void main(String[] args) throws Exception
    {
        GraphBuilder g = new GraphBuilder("src/main/resources/modified/RTripSingle.csv");
        RFP algorithm = new RFP(g.getSimpleDirectedGraph(), g.getVertices());
        Solution solution = algorithm.getSolution();
        solution.print(g.getVertices());

//        FMCS algorithm2 = new FMCS(g.getSimpleDirectedGraph(), g.getVertices());
//        Solution solution2 = algorithm2.getSolution();
//        solution2.printSimple();
    }
}
