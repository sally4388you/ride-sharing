package Controller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class MapAPI: using API to build the path from a source to a destination
 *
 * author: Sally Qi
 * date: 2019/05/12
 */
public class MapAPI {

    private static final String USER_AGENT = "Mozilla/5.0";

    // HTTP GET request
    static public JSONArray openRouteService(String lat1, String lng1, String lat2, String lng2) throws IOException {

        String apiKey = "5b3ce3597851110001cf6248ecd22b074991405097f899bc7fd8f1ac";

        String uri = "https://api.openrouteservice.org/directions?api_key=" + apiKey +
                "&coordinates=" + lng1 + "%2C" + lat1 + "|" + lng2 + "%2C" + lat2 +
                "&preference=shortest&profile=driving-car&geometry_format=geojson";

        System.out.println(uri);

        URL url = new URL(uri);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        JSONArray path = null;

        // optional default is GET
        con.setRequestMethod("GET");

        // add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject obj = new JSONObject(response.toString());
            JSONObject routes = (JSONObject) obj.getJSONArray("routes").get(0);

            JSONObject geometry = routes.getJSONObject("geometry");
            path = geometry.getJSONArray("coordinates");
            path.put(new JSONArray("[ " +
                    routes.getJSONObject("summary").getFloat("distance") +
                    "]"));
        } catch (IOException e) {
            System.out.print("error:");
        }

        return path;
    }
}
