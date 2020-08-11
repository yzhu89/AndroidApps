package com.example.newsgateway;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncArticleTask extends AsyncTask<String, Void, String> {
    private NewsService newsService;
    private String prefix=
            "https://newsapi.org/v2/everything?" +
                    "language=en&pageSize=100&apiKey=d0e5e3a18f5c4149ab07179fa6673bb2&sources=";

    public AsyncArticleTask(NewsService newsService) {
        this.newsService = newsService;
    }

    @Override
    protected String doInBackground(String... params) {
        String sourceName = params[0];
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(prefix + sourceName);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            InputStream s = c.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(s)));

            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append("\n");
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String str) {
        parseJson(str);
    }

    private void parseJson(String s) {
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray jArrSources = jObjMain.getJSONArray("articles");

            for (int i =0; i<jArrSources.length(); i++){
                JSONObject jObjSource = jArrSources.getJSONObject(i);
                String title = jObjSource.getString("title");
                String publishedAt = jObjSource.getString("publishedAt");
                String author = jObjSource.getString("author");
                String description = jObjSource.getString("description");
                String url = jObjSource.getString("url");
                String urlToImage = jObjSource.getString("urlToImage");

                newsService.addArticle(
                        new Article(author, title, description,
                                url, urlToImage, publishedAt, jArrSources.length(), i));
                if (isCancelled()) return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
