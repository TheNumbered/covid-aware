package com.example.covidaware;

import static android.app.PendingIntent.getActivity;
import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.icu.text.SymbolTable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Settings extends AppCompatActivity {
    DatabaseHelper myDB = new DatabaseHelper(this, "app");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        System.out.println(myDB.getToken());
        TextView username = findViewById(R.id.settingsUsername);
        TextView userMail = findViewById(R.id.settingsEmail);

        username.setText(UserData.Username);
        userMail.setText(UserData.Email);

    }
    public void onImageClick(View view) {
        //


        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s2580661/logout.php")).newBuilder();
        urlBuilder.addQueryParameter("token", myDB.getToken());
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                String responseData = "waiting...";
                try {
                    if (response.body() != null) {
                        responseData = response.body().string();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                final String theData = responseData;

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    try {
                        JSONObject feedBack = new JSONObject(theData);
                        String res;
                        if(feedBack.getString("state").equals("400")){
                            myDB.clearDatabase();
                            res =  "Logout Successful";
                            Intent intent = new Intent(Settings.this, launcher.class);
                            startActivity(intent);

                        } else{
                            res = feedBack.getString("res");
                        }
                        Toast.makeText(Settings.this, res, Toast.LENGTH_SHORT).show();
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                });
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
        });

    }
    public void onViewHistory(View view) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s2580661/viewHistory.php")).newBuilder();
        urlBuilder.addQueryParameter("userid", UserData.Id);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseData = response.body().string();
                String[] output;

                try {
                    JSONObject feedBack = new JSONObject(responseData);
                    String res = feedBack.getString("state");
                    if(feedBack.getString("state").equals("400")){

                        JSONArray jsonArray = (JSONArray) feedBack.get("array");
                    output = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        output[i] = "" + jsonObject.getString("loc_name") + "\n";
                        output[i] += "Check In Date: " + jsonObject.getString("check_in_date") + "\n";
                    }

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        LayoutInflater inflater = LayoutInflater.from(Settings.this);
                        View dialogView = inflater.inflate(R.layout.view_history, null);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Settings.this, android.R.layout.simple_list_item_1, output);
                        ListView listView = dialogView.findViewById(R.id.historyList);
                        listView.setAdapter(adapter);

                        Dialog dialog = new Dialog(Settings.this);
                        dialog.setContentView(dialogView);
                        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                        layoutParams.copyFrom(dialog.getWindow().getAttributes());
                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Set your desired width
                        dialog.getWindow().setAttributes(layoutParams);
                        dialog.show();
                    });

                    } else if (feedBack.getString("state").equals("404")) {
                        System.out.println("NO HISTORY");
                    } else {
                        System.out.println(res);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }




//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                final String responseData = response.body().string();
//                String[] output;
//
//                try {
//
//                    JSONArray jsonArray = new JSONArray(responseData);
//                    output = new String[jsonArray.length()];
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        output[i] = "" + jsonObject.getString("loc_name") + "\n";
//                        output[i] += "Check In Date: " + jsonObject.getString("check_in_date") + "\n";
//                    }
//
//                    Handler handler = new Handler(Looper.getMainLooper());
//                    handler.post(() -> {
//                        LayoutInflater inflater = LayoutInflater.from(Settings.this);
//                        View dialogView = inflater.inflate(R.layout.view_history, null);
//
//                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Settings.this, android.R.layout.simple_list_item_1, output);
//                        ListView listView = dialogView.findViewById(R.id.historyList);
//                        listView.setAdapter(adapter);
//
//                        Dialog dialog = new Dialog(Settings.this);
//                        dialog.setContentView(dialogView);
//                        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//                        layoutParams.copyFrom(dialog.getWindow().getAttributes());
//                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Set your desired width
//                        dialog.getWindow().setAttributes(layoutParams);
//                        dialog.show();
//                    });
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//            }
        });
    }

    public void onClearHistory(View view) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s2580661/clearHistory.php")).newBuilder();
        urlBuilder.addQueryParameter("userid", UserData.Id);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                String responseData = "waiting...";
                try {
                    if (response.body() != null) {
                        responseData = response.body().string();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                final String theData = responseData;

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    Toast.makeText(Settings.this, theData, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void onChangeEM(View view){
        Intent intent = new Intent(Settings.this, changeEmail.class);
        startActivity(intent);
    }
    public void onChangePass(View view){
        Intent intent = new Intent(Settings.this, changePassword.class);
        startActivity(intent);
    }
}