package com.example.covidaware;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginTabFragment extends Fragment {
    public LoginTabFragment() {

    }
    private Button loginButton;
    private EditText usernameEntry;
    private EditText passwordEntry;
    DatabaseHelper myDB;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginButton = view.findViewById(R.id.login_button);
        usernameEntry = view.findViewById(R.id.login_username);
        passwordEntry = view.findViewById(R.id.login_password);
        myDB = new DatabaseHelper(getContext(), "app");
        myDB.clearDatabase();


        loginButton.setOnClickListener(v -> {

            String username = usernameEntry.getText().toString();
            String password = passwordEntry.getText().toString();
            if(!username.equals("") && !password.equals("")){
                HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s2580661/logintoapp.php")).newBuilder();
                urlBuilder.addQueryParameter("username", username );
                urlBuilder.addQueryParameter("password", password);
                String url = urlBuilder.build().toString();

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

                                    String token = feedBack.getString("token");
                                    String insertQuery = "Insert into tokens values (1, '" + token + "', 0)";

                                    SQLiteDatabase db = myDB.getWritableDatabase();
                                    db.execSQL(insertQuery);
                                    db.close();
                                    //UserData.token = token;

                                    Intent intent = new Intent(getContext(),MainActivity.class);
                                    startActivity(intent);
                                } else if (feedBack.getString("state").equals("401")) {
                                    res = "User does not exist";
                                }else if(feedBack.getString("state").equals("402")){
                                    res = "Incorrect password";
                                }else{
                                    res = "Something went wrong";
                                }
                                Toast.makeText(getContext(), res, Toast.LENGTH_SHORT).show();
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
        });

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_tab, container, false);
    }

}