package Model;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Class Solution: a solution for a ride sharing problem
 *
 * author: Sally Qi
 * date: 2019/05/12
 */
public class Solution
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

    public void setS(Set<Integer> s) {
        this.S = new HashSet<>();
        S.addAll(s);
    }

    public void setSigma(Map<Integer, Set<Integer>> sigma) {
        this.sigma = new HashMap<>();
        this.sigma.putAll(sigma);
    }

    public Set<Integer> getSigmaAsSet() {
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

    public void addPassengersToADriver(int labelId, Set<Integer> S) {
        Set<Integer> set = new HashSet<>(S);
        if (sigma.containsKey(labelId)) {
            set.addAll(sigma.get(labelId));
        }
        this.sigma.put(labelId, set);
    }

    /**
     * Add an element to σ(tripId)
     *
     * @param tripId trip id
     * @param passenger passenger id
     */
    public void addPassengerToADriver(int tripId, int passenger)
    {
        Set<Integer> set = (sigma.containsKey(tripId)) ? sigma.get(tripId) : new HashSet<>();
        set.add(passenger);

        this.sigma.put(tripId, set);
    }

    /**
     * Remove an element from σ(tripId)
     *
     * @param tripId trip id
     * @param passenger passenger id
     */
    public void removePassengerFromDriver(int tripId, int passenger)
    {
        if (!sigma.containsKey(tripId)) return ;

        Set<Integer> set = sigma.get(tripId);
        set.remove(passenger);

        this.sigma.put(tripId, set);
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
}
