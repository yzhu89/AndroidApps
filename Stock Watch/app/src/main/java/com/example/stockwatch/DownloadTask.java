package com.example.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Double, String> {
    private static final String TAG = "DownloadTask";
    private MainActivity mainActivity;
    private String stockURL;
    private String downloadURL = "https://cloud.iexapis.com/stable/stock/";
    private String request = "GET";
    private int position;
    private String symbol;
    private String name;
    private static final String yourAPIKey = "/quote?token=pk_b57ca92f615548c081c72300b8a3bad3";

    public DownloadTask(MainActivity ma, int pos){
        this.mainActivity = ma;
        this.position = pos;
    }


    @Override
    protected String doInBackground(String... strArgs) {
        this.symbol = strArgs[0];
        this.name = strArgs[1];

        //downloadURL = downloadURL + symbol +
        // "/quote?token=API Token: pk_b57ca92f615548c081c72300b8a3bad3";
        stockURL = downloadURL + symbol + yourAPIKey;

        Uri stockURI = Uri.parse(stockURL);
        String urlToUse = stockURI.toString();
        Log.d(TAG, "doInBackground: " + urlToUse);
        StringBuilder sb = new StringBuilder();

        try{
            URL url = new URL(urlToUse);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod(request);

            InputStream inputStream = c.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line =  bufferedReader.readLine()) != null){
                sb.append(line).append("\n");
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

        try {
            JSONObject j = new JSONObject(str);
            double latestPrice = Double.valueOf(j.getDouble("latestPrice"));
            double change = Double.valueOf(j.getDouble("change"));
            double changePercent = Double.valueOf(j.getDouble("changePercent"));

            Stock stock = new Stock(this.symbol, this.name, latestPrice, change, changePercent);

            if (this.position > -1) { //stock exist
                this.mainActivity.updateStock(stock, this.position);
            }
            else {
                this.mainActivity.addStock(stock);
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }
}
