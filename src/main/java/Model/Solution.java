package Model;

import org.apache.commons.lang3.StringUtils;
import java.util.*;

/**
 * Class Solution: a solution for a ride sharing problem
 *
 * author: Sally Qi
 * date: 2019/05/12
 */
public class Solution implements Cloneable
{
    private Set<Integer> S;
    private Map<Integer, Set<Integer>> sigma;

    /**
     * Initialize variables S and σ
     */
    public Solution()
    {
        this.S = new HashSet<>();
        this.sigma = new HashMap<>();
    }

    public Set<Integer> getS() {
        return S;
    }

    public Map<Integer, Set<Integer>> getSigma() {
        return sigma;
    }

    public Set<Integer> getAllPassengers()
    {
        Set<Integer> result = new HashSet<>();
        for (Set<Integer> set : sigma.values()) {
            result.addAll(set);
        }
        return result;
    }

    public void addDriverToS(int driver) {
        this.S.add(driver);
    }

    public void removeDriverFromS(int driver) {
        this.S.remove(driver);
    }

    public void addPassengersToADriver(int label, Set<Integer> S)
    {
        if (!sigma.containsKey(label)) {
            Set<Integer> set = new HashSet<>();
            sigma.put(label, set);
        }

        sigma.get(label).addAll(S);
    }

    /**
     * Add an element to σ(tripId)
     *
     * @param driver trip id
     * @param passenger passenger id
     */
    public void addPassengerToADriver(int driver, int passenger)
    {
        Set<Integer> set = (sigma.containsKey(driver)) ? sigma.get(driver) : new HashSet<>();
        set.add(passenger);

        this.sigma.put(driver, set);
    }

    /**
     * Remove an element from σ(tripId)
     *
     * @param driver trip id
     * @param passenger passenger id
     */
    public void removePassengerFromDriver(int driver, int passenger)
    {
        if (!sigma.containsKey(driver)) return ;

        Set<Integer> set = sigma.get(driver);
        set.remove(passenger);

        this.sigma.put(driver, set);
    }

    /**
     * @return size of S
     */
    public int lengthOfS()
    {
        return this.S.size();
    }

    /**
     * @param driver trip id
     * @return size of σ(driver)
     */
    public int lengthOfSigma(int driver)
    {
        return this.sigma.get(driver).size();
    }

    /**
     * Print solution for the given ride sharing problem
     *
     * @param trips ArrayList<Vertex>
     */
    public void print(ArrayList<Vertex> trips)
    {
        // print original trips data
        System.out.println("\nTrip data:");
        for (Vertex vertex: trips) {
            Trip trip = vertex.getTrip();
            System.out.println("Trip " + trip.getTripId() + "(" + trip.getCapacity() + "): " +
                    "From (" + trip.getStartLat() + ", " + trip.getStartLng() + ") " +
                    "to (" + trip.getEndLat() + ", " + trip.getEndLng() + ") " +
                    "having " + trip.getLength() + "m");
        }

        // print solution
        System.out.println("\nSolution:");
        for (int i: this.S) {
            System.out.print("Trip " + trips.get(i).getTrip().getTripId() + " serves: " + "trip (");
            System.out.print(StringUtils.join(this.sigma.get(i), ", "));
            System.out.print(")\n");
        }
    }

    /**
     * Print solution only with its id
     */
    public void printSimple()
    {
        System.out.println("Solution:\n");
        System.out.println("Driver Set:" + this.S.toString());
        System.out.println("Passenger Set: {");
        for (int i: this.sigma.keySet()) {
            System.out.println("\t" + i + ": " + this.sigma.get(i).toString());
        }
        System.out.println("}");
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
