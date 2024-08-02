package com.example.covidaware;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

public class HomeFragment extends Fragment {
    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        return rootView;
    }
    private LinearLayout locationContainer;
    private EditText searchEditText;
    private BottomNavigationView bottomNavigationView;
    private List<Location> locations;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locationContainer = view.findViewById(R.id.locationContainer);
        searchEditText = view.findViewById(R.id.searchEditText);

        locations = LocationData.locations;
        if(locations == null){
            LocationData.locations = new ArrayList<>();
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
                                if(infected>LocationData.maxInfected) LocationData.maxInfected = infected;
                                String id = jsonObject.getString("location_id");
                                Location location = new Location(id,locationName, address, latitude, longitude, infected);
                                LocationData.locations.add(location);
                            }
                            locations = LocationData.getLocations();
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> {
                                for(Location location: LocationData.getLocations()){
                                    addLocation(location);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        else{
            for (Location location : locations) {
                addLocation(location);
            }
        }


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchTerm = charSequence.toString().toLowerCase();
                filterLocations(searchTerm);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    private void colorInfected(RelativeLayout infectedView, int infected) {
        if (infected == 0) {
            infectedView.setBackgroundColor(0x3388CC99);//0xFF88CC99 also good
        } else if (infected <= LocationData.maxInfected / 2) {
            infectedView.setBackgroundColor(0x33DDCC88);//0xFFDDCC88
        } else {
            infectedView.setBackgroundColor(0x33CC8888);//0xFFCC8888
        }
    }



    private void addLocation(Location location) {
        RelativeLayout locationObject = new RelativeLayout(getContext());
        locationObject.setLayoutParams(new RelativeLayout.LayoutParams(-1, -2));
        locationObject.setPadding(10,30,10,30);

        TextView locationNameView = new TextView(getContext());
        locationNameView.setId(View.generateViewId());
        locationNameView.setText(location.getLocationName());
        locationNameView.setTypeface(null, Typeface.BOLD);
        locationNameView.setTextSize(16);

        TextView infectedView = new TextView(getContext());
        colorInfected(locationObject,location.getInfected());
        infectedView.setText(String.valueOf(location.getInfected()));
        infectedView.setTypeface(null, Typeface.BOLD);
        infectedView.setPadding(20,10,20,10);

        TextView addressView = new TextView(getContext());
        addressView.setText(location.getAddress());

        RelativeLayout.LayoutParams right = new RelativeLayout.LayoutParams(-2, -2);
        right.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        RelativeLayout.LayoutParams below = new RelativeLayout.LayoutParams(-2, -2);
        below.addRule(RelativeLayout.BELOW, locationNameView.getId());

        locationObject.addView(locationNameView);
        locationObject.addView(infectedView, right);
        locationObject.addView(addressView, below);
        locationContainer.addView(locationObject);
    }
    private void filterLocations(String searchTerm) {
        locationContainer.removeAllViews();
        for (Location location : locations) {
            if (location.getLocationName().toLowerCase().contains(searchTerm) || location.getAddress().toLowerCase().contains(searchTerm)) {
                addLocation(location);
            }
        }
    }
}
