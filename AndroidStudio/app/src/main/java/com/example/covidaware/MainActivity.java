package com.example.covidaware;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment initialFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.contentFrame, initialFragment).commit();

        BottomNavigationView  bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;

            if (R.id.menu_item1 == item.getItemId()) selectedFragment = new HomeFragment();
            else if (R.id.menu_item2 == item.getItemId()) selectedFragment = new Report();
            else selectedFragment = new MapsFragment();

            getSupportFragmentManager().beginTransaction().replace(R.id.contentFrame, selectedFragment).commit();
            return true;
        });
        FloatingActionButton menuButton = findViewById(R.id.FloatingMenu);
        menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if(item.getItemId() == R.id.mcheckIn){
                    showDialogBox(R.layout.check_in);
                    return true;
                }
                else if(item.getItemId() == R.id.mreportCase) {
                    showDialogBox(R.layout.report_case);
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }
    public void onImageClick(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
    public  void showDialogBox(int LayoutID){
        AlertDialog.Builder dialogBox = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(LayoutID,null);
        dialogBox.setView(dialogView);

        Button dateButton = null;
        AutoCompleteTextView addressEntry = null,locationEntry = null;
        if(LayoutID == R.layout.check_in){
            dateButton = dialogView.findViewById(R.id.checkInDate);
            addressEntry = dialogView.findViewById(R.id.checkInAddress);
            locationEntry = dialogView.findViewById(R.id.checkInLocation);

            // Here am setting the auto_complete
            ArrayAdapter<String> addressAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, LocationData.getLocationAddress());
            ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, LocationData.getLocationNames());
            addressEntry.setAdapter(addressAdapter);
            locationEntry.setAdapter(nameAdapter);
        }
        else if(LayoutID == R.layout.report_case){
            dateButton = dialogView.findViewById(R.id.reportDate);
        }

        if(dateButton != null){
            Button finalDateButton = dateButton;
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowDatePicker(finalDateButton);
                }
            });
        }
        dialogBox.setNegativeButton("Cancel",null);
        AutoCompleteTextView finalAddressEntry = addressEntry;
        AutoCompleteTextView finalLocationEntry = locationEntry;
        Button finalDateButton = dateButton;

        OkHttpClient client = new OkHttpClient();
        final String[] url = new String[1];
        dialogBox.setPositiveButton("Submit", (dialog, which) -> {
            if(LayoutID == R.layout.check_in){
                String address = finalAddressEntry.getText().toString();
                String name = finalLocationEntry.getText().toString();
                String locationId = "null";

                if(!address.equals("")){
                    for(com.example.covidaware.Location place : LocationData.getLocations()){
                        if(place.getAddress().equals(address)){locationId = place.getId();break;}
                    }
                } else if (!name.equals("")) {
                    for (com.example.covidaware.Location place: LocationData.getLocations()){
                        if(place.getLocationName().equals(name)){locationId = place.getId();break;}
                    }
                }
                else {
                    Toast.makeText(this,"Enter a Location Name or Address",Toast.LENGTH_LONG);
                    return;
                }
                if(locationId.equals("null")) {Toast.makeText(this,"Invalid Location",Toast.LENGTH_LONG);return;}

                HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s2580661/checkin.php").newBuilder();
                urlBuilder.addQueryParameter("location_id",locationId);
                urlBuilder.addQueryParameter("date", finalDateButton.getText().toString());
                urlBuilder.addQueryParameter("id",UserData.Id);
                url[0] = urlBuilder.build().toString();
            } else if (LayoutID == R.layout.report_case) {
                HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s2556833/reportcase.php").newBuilder();
                urlBuilder.addQueryParameter("date",finalDateButton.getText().toString());
                urlBuilder.addQueryParameter("id", UserData.Id);
                url[0] = urlBuilder.build().toString();
            }

            Request request = new Request.Builder().url(url[0]).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    // Read data on the worker thread
                    final String responseData = response.body().string();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        Toast.makeText(getApplicationContext(),responseData, Toast.LENGTH_SHORT).show();
                        LocationData.initializeLocations();
                    });
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                }
            });

        });
        dialogBox.create();
        dialogBox.show();
    }

    public void ShowDatePicker(Button button){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (DatePickerDialog.OnDateSetListener) (view, year1, month1, dayOfMonth) -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String selected = dateFormat.format(calendar.getTime());
            button.setText(selected);
        },year,month,day);
        datePickerDialog.show();
    }

    public void onNotification(View view) {
        AlertDialog.Builder dialogBox = new AlertDialog.Builder(this);
        dialogBox.setTitle("Notification");
        dialogBox.setNegativeButton("Cancel",null);
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s2556833/testingSite.php").newBuilder();
        urlBuilder.addQueryParameter("user_id",UserData.Id);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(json);
                        String message = "You have been in contact with an infected person we suggest you get test: \n Testing Sites \n \n";
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            message += jsonObject.getString("loc_name") +"\n";
                            message += "Open at :" + jsonObject.getString("opening_time") + "\n";
                            message += "Closes at :" + jsonObject.getString("closing_time") + "\n";
                        }
                        dialogBox.setMessage(message);
                        dialogBox.create();
                        dialogBox.show();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "User has not been to any location.", Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });
    }

}
