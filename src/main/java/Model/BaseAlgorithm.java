package Model;

/**
 * Interface BaseAlgorithm: every algorithm needs to have these three functionality
 *
 * author: Sally Qi
 * date: 2019/05/12
 */
public interface BaseAlgorithm
{
    /**
     * @return a solution for a given road network G and a set of trips R
     */
    Solution getSolution();

    /**
     * Evaluate performance of the algorithm with number of drivers
     *
     * @return int
     */
    int evaluateNumberOfDrivers();

    /**
     * Evaluate performance of the algorithm with distance
     *
     * @return double
     */
    double evaluateDistance();
}
