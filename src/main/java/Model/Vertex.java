package Model;

import java.awt.geom.Point2D;

/**
 * Class Vertex: present a vertex in a road network G
 *
 * author: Sally Qi
 * date: 2019/05/12
 */
public class Vertex
{
    private int id;
    private Point2D.Double location;
    private Trip trip;

    public Vertex(int id, Point2D.Double location, Trip trip)
    {
        this.id = id;
        this.location = location;
        this.trip = trip;
    }

    /**
     * Store longitude and latitude of the source of a trip
     *
     * @return Point2D.Double (longitude, latitude)
     */
    public Point2D.Double getLocation() {
        return this.location;
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
