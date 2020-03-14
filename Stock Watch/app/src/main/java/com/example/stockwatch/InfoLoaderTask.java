package com.example.stockwatch;

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
import java.net.URL;
import java.util.HashMap;

public class InfoLoaderTask extends AsyncTask<String, Double, String> {

    private MainActivity mainActivity;
    private String infoURL = "https://api.iextrading.com/1.0/ref-data/symbols";
    private String request = "GET";

    private static final String TAG = "NameDownloader";


    public InfoLoaderTask(MainActivity ma){
        this.mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... strArgs){
        Uri infoURI = Uri.parse(infoURL);
        String urlToUse = infoURI.toString();
        StringBuilder sb = new StringBuilder();
        Log.d(TAG, "doInBackground: " + urlToUse);

        try{
            URL url = new URL(urlToUse);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod(request);

            InputStream inputStream = c.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = bufferedReader.readLine()) != null){
                sb.append(line).append('\n');
            }

            return sb.toString();
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String str){
        super.onPostExecute(str);

        HashMap<String, String> map = this.parseJSON(str);
        this.mainActivity.generateHashMap(map);
    }

    private HashMap<String, String> parseJSON(String str){
        HashMap<String, String> parsedMap = new HashMap<>();

        try{
            JSONArray jArray = new JSONArray(str);

            Log.d(TAG, "parseJSON: ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            for(int i = 0; i < jArray.length(); i++){
                JSONObject stock = jArray.getJSONObject(i);
                String name = stock.getString("name");
                if(!name.isEmpty()){
                    String symbol = stock.getString("symbol");
                    parsedMap.put(symbol, name);
                    Log.d(TAG, "parseJSON: Finished");
                }
            }
            return parsedMap;
        }
        catch(JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}
