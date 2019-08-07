package Model;

import Controller.MapAPI;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.json.JSONArray;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Miscellaneous
{
    /**
     * Generate standard csv for this program from another csv file.
     *
     * @throws Exception throw file not found exception
     */
    static public void generateFromCSVFile() throws Exception {
        String home_dir = System.getProperty("user.dir");

        String readUrl = home_dir + "/src/main/resources/origin/SampleData.csv";
        String writeUrl = home_dir + "/src/main/resources/modified/RTrip2.csv";

        System.out.println(readUrl);

        CSVReader reader = new CSVReader(new FileReader(readUrl));
        CSVWriter writer = new CSVWriter(new FileWriter(writeUrl), ',');
        String[] nextLine;
        int[] index = {47, 48, 49, 50, 51, 52, 55, 56, 57, 58};
        int count = 0;
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            String[] entries = new String[20];
            if (count++ == 0) {
                entries = setTitle();
                writer.writeNext(entries);
                continue;
            }
            int j = 0;

            entries[j++] = String.valueOf(count);
            entries[j++] = "0";

            for (int i : index) {
                entries[j++] = nextLine[i];
            }

            JSONArray path = MapAPI.openRouteService(entries[8], entries[9], entries[10], entries[11]);
            if (path == null) {
                count--;
                System.out.println(nextLine[47]);
                continue;
            }

            entries = setPath(entries, j, path, null);

            // feed in your array (or convert your data to an array)
            writer.writeNext(entries);
        }
        writer.close();
    }

    /**
     * Generate a csv file where all trip are in a single line
     *
     * @param startLat     start latitude
     * @param startLng     start longitude
     * @param endLat       end latitude
     * @param endLng       end longitude
     * @param amount       how many rows are going to be generated
     * @param intersection how many intersections are going to be generated
     * @param pathPlus     idk
     * @param startFrom    idk
     * @throws Exception throw file not found exception
     */
    static public void generateSinglePath(double startLat, double startLng, double endLat, double endLng, int amount,
                                          int intersection, JSONArray pathPlus, int startFrom) throws Exception {

        String home_dir = System.getProperty("user.dir");
        String writeUrl = home_dir + "/src/main/resources/modified/RTripSingle.csv";

        CSVWriter writer = new CSVWriter(new FileWriter(writeUrl, true), ',');

        if (startFrom == 0) {
            String[] titles = setTitle();
            writer.writeNext(titles);
        }

        int count = 0;
        double[] startLngs = new double[amount];
        double[] startLats = new double[amount];
        startLngs[0] = startLng;
        startLats[0] = startLat;

        JSONArray path = MapAPI.openRouteService(String.valueOf(startLat), String.valueOf(startLng),
                String.valueOf(endLat), String.valueOf(endLng));
        Random r = new Random();

        if (path != null) {
            for (int i = 1; i < amount; i++) {
                int randomIndex = r.nextInt(path.length());
                JSONArray start = (JSONArray) path.get(randomIndex);

                startLats[i] = start.getDouble(1);
                startLngs[i] = start.getDouble(0);
            }
        }

        while (count < amount) {
            String[] entries;

            entries = setData(startLats[count], startLngs[count], endLat, endLng, startFrom++, pathPlus);
            if (entries == null) {
                count++;
                continue;
            }

            // feed in your array (or convert your data to an array)
            writer.writeNext(entries);
            count++;
        }
        writer.close();

        if (intersection != 0) { // create a joint point in the inverse tree
            GeoLocation thisLocation = null;
            int randomIndex = 1;
            while (pathPlus == null) {
                thisLocation = GeoLocation.fromDegrees(startLats[randomIndex++], startLngs[randomIndex++]);
                pathPlus = MapAPI.openRouteService(String.valueOf(startLats[1]), String.valueOf(startLngs[1]),
                        String.valueOf(endLat), String.valueOf(endLng));
            }

            for (int i = 0; i < intersection; i++) {
                GeoLocation[] geo =
                        thisLocation.boundingCoordinates(10, 6371.01);
                generateSinglePath(geo[0].getLatitudeInDegrees(), geo[0].getLongitudeInDegrees(), startLats[1],
                        startLngs[1], 4, 0, pathPlus, count);
            }
        }
    }

    /**
     * Set title for csv file
     *
     * @return array
     */
    static private String[] setTitle() {
        String[] entries = new String[20];
        int j = 0;

        entries[j++] = "id";
        entries[j++] = "tripcapacity";
        entries[j++] = "tripid";
        entries[j++] = "tripaction";
        entries[j++] = "tripstarttimestamp";
        entries[j++] = "tripendtimestamp";
        entries[j++] = "tripcompleted";
        entries[j++] = "tripduration";
        entries[j++] = "tripstartlat";
        entries[j++] = "tripstartlng";
        entries[j++] = "tripendlat";
        entries[j++] = "tripendlng";
        entries[j++] = "mapstartlat";
        entries[j++] = "mapstartlng";
        entries[j++] = "mapendlat";
        entries[j++] = "mapendlng";
        entries[j++] = "tripdetour";
        entries[j++] = "tripprice";
        entries[j++] = "trippath";
        entries[j] = "triplength";
        return entries;
    }

    /**
     * Set data for csv
     *
     * @param startLat start latitude
     * @param startLng start longitude
     * @param endLat   end latitude
     * @param endLng   end longitude
     * @param count    idk
     * @param pathPlus idk
     * @return idk
     * @throws Exception idk
     */
    static private String[] setData(double startLat, double startLng, double endLat, double endLng, int count,
                                    JSONArray pathPlus) throws Exception {
        String[] entries = new String[20];
        int j = 0;
        entries[j++] = String.valueOf(count);
        entries[j++] = String.valueOf(new Random().nextInt(5));
        entries[j++] = String.valueOf(count);
        entries[j++] = "0";

        String timeISO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmX")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());

        entries[j++] = timeISO;
        entries[j++] = timeISO;
        entries[j++] = "0";
        entries[j++] = "0";
        entries[j++] = String.valueOf(startLat);
        entries[j++] = String.valueOf(startLng);
        entries[j++] = String.valueOf(endLat);
        entries[j++] = String.valueOf(endLng);
        JSONArray path = MapAPI.openRouteService(String.valueOf(startLat), String.valueOf(startLng),
                String.valueOf(endLat), String.valueOf(endLng));
        if (path == null) {
            System.out.println(count);
            return null;
        }
        entries = setPath(entries, j, path, pathPlus);

        return entries;
    }

    static private String[] setPath(String[] entries, int j, JSONArray path, JSONArray pathPlus) {
        Double length = (Double) ((JSONArray) path.get(path.length() - 1)).get(0);
        path.remove(path.length() - 1);

        if (pathPlus != null) {
            length += (Double) ((JSONArray) pathPlus.get(pathPlus.length() - 1)).get(0);
            for (int i = 2; i < pathPlus.length() - 1; i++) {
                path.put(pathPlus.getJSONArray(i));
            }
        }

        JSONArray start = (JSONArray) path.get(0);
        JSONArray end = (JSONArray) path.get(path.length() - 1);

        String mapstartlat = String.valueOf(start.get(1));
        String mapstartlng = String.valueOf(start.get(0));
        String mapendlat = String.valueOf(end.get(1));
        String mapendlng = String.valueOf(end.get(0));

        entries[j++] = mapstartlat;
        entries[j++] = mapstartlng;
        entries[j++] = mapendlat;
        entries[j++] = mapendlng;
        entries[j++] = "0";
        entries[j++] = "0";
        entries[j++] = path.toString();
        entries[j] = String.valueOf(length);

        return entries;
    }
}
