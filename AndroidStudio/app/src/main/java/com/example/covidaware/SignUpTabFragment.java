package com.example.covidaware;

import android.content.Intent;
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


public class SignUpTabFragment extends Fragment {

    public SignUpTabFragment() {
        // Required empty public constructor
    }

    private Button signupButton;
    private EditText passwordEntry;
    private EditText confirmEntry;
    private EditText emailEntry;
    private EditText usernameEntry;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signupButton = view.findViewById(R.id.signup_button);
        passwordEntry = view.findViewById(R.id.signup_password);
        confirmEntry = view.findViewById(R.id.signup_confirm);
        emailEntry = view.findViewById(R.id.signup_email);
        usernameEntry = view.findViewById(R.id.signup_username);


        signupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String password = passwordEntry.getText().toString();
                String email = emailEntry.getText().toString();
                String confirm = confirmEntry.getText().toString();
                String username = usernameEntry.getText().toString();

                if(!(password.equals("") || email.equals("") || confirm.equals("") || username.equals(""))){
                    if(password.equals(confirm)){
                        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s2580661/mcClasses/signin.php")).newBuilder();
                        urlBuilder.addQueryParameter("username", username );
                        urlBuilder.addQueryParameter("email",email);
                        urlBuilder.addQueryParameter("password", password);
                        String url = urlBuilder.build().toString();

                        Request request = new Request.Builder()
                                .url(url)
                                .build();

                        OkHttpClient client = new OkHttpClient();
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
                                    try {
                                        JSONObject feedBack = new JSONObject(theData);
                                        String res;
                                        if(feedBack.getString("status").equals("400")){
                                            res =  "Sign Up Successful";
                                            UserData.Username = username;
                                            UserData.Email = email;
                                            UserData.Status = "Unknown";
                                            Intent intent = new Intent(getContext(),MainActivity.class);
                                            startActivity(intent);
                                        } else if (feedBack.getString("status").equals("401")) {
                                            res = feedBack.getString("words");
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
                        });
                    }
                    else Toast.makeText(getContext(), "Confirm Password and Password do not match"+":"+password+":"+confirm, Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getContext(), "Missing fields", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup_tab, container, false);
    }
}