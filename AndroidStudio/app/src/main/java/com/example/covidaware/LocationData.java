package com.example.covidaware;

import android.text.style.UpdateLayout;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationData {
    public static List<Location> locations;
    public static int maxInfected = 0;

    public static List<Location> getLocations() {
        if (locations == null) {
            initializeLocations();
        }
        return locations;
    }
    public static String[] getLocationAddress(){
        String[] names = new String[locations.size()];
       for(int i=0; i<locations.size(); i++) names[i] = locations.get(i).getAddress();
       return names;
    }

    public static String[] getLocationNames(){
        String[] names = new String[locations.size()];
        for(int i=0; i<locations.size(); i++) names[i] = locations.get(i).getLocationName();
        return names;
    }

    public static void initializeLocations() {
        locations = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s2556833/getlocations.php").newBuilder();
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle failure
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();

                    try {
                        JSONArray jsonArray = new JSONArray(json);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String locationName = jsonObject.getString("loc_name");
                            String address = jsonObject.getString("loc_address");
                            double latitude = jsonObject.getDouble("loc_latitude");
                            double longitude = jsonObject.getDouble("loc_longitude");
                            int infected = jsonObject.getInt("infected");
                            if(infected>maxInfected) maxInfected = infected;
                            String id = jsonObject.getString("location_id");

                            Location location = new Location(id,locationName, address, latitude, longitude, infected);
                            locations.add(location);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
