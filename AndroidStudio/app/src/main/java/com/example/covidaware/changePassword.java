package com.example.covidaware;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class changePassword extends AppCompatActivity {
    String NewPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
    }

    public void onSubmit(View view) {
        TextView newPass = findViewById(R.id.editTextTextNewPassword);
        NewPassword = newPass.getText().toString();

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s2580661/changePassword.php")).newBuilder();
        urlBuilder.addQueryParameter("userid", UserData.Id);
        urlBuilder.addQueryParameter("newpass", NewPassword);
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
                    Toast.makeText(changePassword.this, theData, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}