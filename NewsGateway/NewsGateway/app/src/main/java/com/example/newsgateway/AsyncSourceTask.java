package com.example.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class AsyncSourceTask extends AsyncTask<String, Void, String>{

    private MainActivity mainActivity;
    private String prefix =
            "https://newsapi.org/v2/sources?language=en" +
                    "&country=us&apiKey=d0e5e3a18f5c4149ab07179fa6673bb2&category=";

    public AsyncSourceTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    protected String doInBackground(String... params) {
        String category = params[0];
        StringBuilder sb = new StringBuilder();

        try {
            URL urlToUse = new URL(prefix+category);
            HttpURLConnection c = (HttpURLConnection) urlToUse.openConnection();
            c.setRequestMethod("GET");
            InputStream inputStream = c.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(inputStream)));

            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append("\n");
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        parseJson(s);
    }

    private void parseJson(String s) {
        try {
            JSONObject jObject = new JSONObject(s);
            JSONArray jSourceList = jObject.getJSONArray("sources");
            mainActivity.clearSource();

            for (int i =0; i<jSourceList.length(); i++){
                JSONObject jSource = jSourceList.getJSONObject(i);
                String id = jSource.getString("id");
                String name = jSource.getString("name");
                String url = jSource.getString("url");
                String category = jSource.getString("category");

                NewsSource newsSource = new NewsSource(id, name, url, category);
                mainActivity.addSource(newsSource);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
