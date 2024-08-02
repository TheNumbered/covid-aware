package com.example.covidaware;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ReportFragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Report extends Fragment {


    public Report() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s2580661/queryStatus.php").newBuilder();
        urlBuilder.addQueryParameter("user_id",UserData.Id);
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
                            output[i] += "Infected: " + jsonObject.getString("infected") + "\n";
                        }

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, output);

                            ListView listView = view.findViewById(R.id.listReport);
                            listView.setAdapter(adapter);
                        });
                    } else if (feedBack.getString("state").equals("404")) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "NO HISTORY", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), res, Toast.LENGTH_SHORT).show();
                        });

                    }
                } catch (JSONException e) {
                    getActivity().runOnUiThread(()->{
                        Toast.makeText(getContext(), "Something went wrong, Try again later", Toast.LENGTH_SHORT).show();
                    });
                }

            }
        });



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_report, container, false);
    }
}