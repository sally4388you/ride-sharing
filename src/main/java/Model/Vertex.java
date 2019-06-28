package Model;

/**
 * Class Vertex: present a vertex in a road network G
 *
 * author: Sally Qi
 * date: 2019/05/12
 */
public class Vertex
{
    private int id;
    private Trip trip;

    public Vertex(int id, Trip trip)
    {
        this.id = id;
        this.trip = trip;
    }

    /**
     * Store all the information of a trip
     *
     * @return a trip
     */
    public Trip getTrip() {
        return this.trip;
    }

    /**
     * Store trip id
     *
     * @return trip id
     */
    public int getId() {
        return id;
    }
}
