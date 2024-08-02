package com.example.covidaware;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

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

public class launcher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseHelper myDB = new DatabaseHelper(this, "app");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        String token = myDB.getToken();
        System.out.println("my token ="+ token);
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s2580661/confirmToken.php")).newBuilder();
        urlBuilder.addQueryParameter("token", token );
        String url = urlBuilder.build().toString();

        //System.out.println(url);
        Request request = new Request.Builder().url(url).build();

        OkHttpClient client = new OkHttpClient();
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
                            res =  "Login Successful";

                            UserData.Status = "Not sure";
                            UserData.Email = feedBack.getString("email");
                            UserData.Username = feedBack.getString("username");
                            UserData.Id = feedBack.getString("id");
                            Intent intent = new Intent(launcher.this,MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(launcher.this, res, Toast.LENGTH_SHORT).show();
                        } else{

                            myDB.clearDatabase();
                            Intent intent = new Intent(launcher.this,Login.class);
                            startActivity(intent);
                        }

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
}