package Model;

import com.opencsv.bean.CsvBindByName;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;

/**
 * Class trip: store original information for a trip
 *
 * author: Sally Qi
 * date: 2019/05/12
 */
public class Trip
{
    @CsvBindByName(column = "id")
    private long id;
    @CsvBindByName(column = "tripid")
    private String tripId;

    @CsvBindByName(column = "carid")
    private int carId;
    @CsvBindByName(column = "tripcapacity")
    private int capacity;

    @CsvBindByName(column = "tripaction")
    private int action;
    @CsvBindByName(column = "tripstarttimestamp")
    private String startTime;
    @CsvBindByName(column = "tripendtimestamp")
    private String endTime;
    @CsvBindByName(column = "tripcompleted")
    private boolean completed;
    @CsvBindByName(column = "tripduration")
    private String duration;
    @CsvBindByName(column = "tripstartlat")
    private String startLat;
    @CsvBindByName(column = "tripstartlng")
    private String startLng;
    @CsvBindByName(column = "tripendlat")
    private String endLat;
    @CsvBindByName(column = "tripendlng")
    private String endLng;

    @CsvBindByName(column = "mapstartlat")
    private String mapStartLat;
    @CsvBindByName(column = "mapstartlng")
    private String mapStartLng;
    @CsvBindByName(column = "mapendlat")
    private String mapEndLat;
    @CsvBindByName(column = "mapendlng")
    private String mapEndLng;

    @CsvBindByName(column = "tripdetour")
    private int detour;
    @CsvBindByName(column = "tripprice")
    private int price;
    @CsvBindByName(column = "trippath")
    private String path;
    @CsvBindByName(column = "triplength")
    private Double length;

    public long getId() {
        return id;
    }

    public String getTripId() {
        return tripId;
    }

    public int getCarId() {
        return carId;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getAction() {
        return action;
    }

    public DateTime getStartTime() {
        org.joda.time.format.DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateHourMinuteSecondFraction();

        return dateTimeFormatter.parseDateTime(startTime);
    }

    public DateTime getEndTime() {
        org.joda.time.format.DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateHourMinuteSecondFraction();

        return dateTimeFormatter.parseDateTime(endTime);
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getDuration() {
        LocalTime time = LocalTime.parse(duration) ;
        return time.getMinuteOfHour();
    }

    public String getStartLat() {
        return startLat;
    }

    public String getStartLng() {
        return startLng;
    }

    public String getEndLat() {
        return endLat;
    }

    public String getEndLng() {
        return endLng;
    }

    String getMapStartLat() {
        return mapStartLat;
    }

    String getMapStartLng() {
        return mapStartLng;
    }

    String getMapEndLat() {
        return mapEndLat;
    }

    String getMapEndLng() {
        return mapEndLng;
    }

    public int getDetour() {
        return detour;
    }

    public int getPrice() {
        return price;
    }

    public JSONArray getPath() {
        return new JSONArray(path);
    }

    public Double getLength() {
        return length;
    }

    public void setId(long id) {
        this.id = id;
    }
}
