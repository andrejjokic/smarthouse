/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.planner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import smarthouse.entities.Location;

/**
 *
 * @author Andrej
 */


public class DistanceCalculator {
    
    private static final String URI = "https://maps.googleapis.com/maps/api/distancematrix/json";
    private static final String API_KEY = "AIzaSyANRUuBqu_N3PW_4sIUUINqmoLKTw1CP00";
    
    public int calculateTime(Location locA,Location locB) {

        if (locA == null || locB == null) return 0;
        
        String origins = locA.getLatitude() + "," + locA.getLongitude();
        String destinations = locB.getLatitude() + "," + locB.getLongitude();
        
        Client client = ClientBuilder.newClient();

        String json = client.target(URI)
                .queryParam("origins", origins)
                .queryParam("destinations", destinations)
                .queryParam("key", API_KEY)
                .request()
                .get(String.class);
        
        client.close();
        
        return convertJSON(json);
    }
    
    private int convertJSON(String json) {
     
        int travelTimeInSeconds = new JSONObject(json)                //whole package
                .getJSONArray("rows")       //rows array
                .getJSONObject(0)           //rows element
                .getJSONArray("elements")   //elements array
                .getJSONObject(0)           //elements element
                .getJSONObject("duration")  //duration
                .getInt("value"); //Travel time in seconds
        
        return travelTimeInSeconds / 60;            //return value is in minutes
    }
}
