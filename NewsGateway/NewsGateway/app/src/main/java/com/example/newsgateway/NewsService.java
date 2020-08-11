package com.example.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class NewsService extends Service{

    private ServiceReceiver serviceReceiver;
    private boolean running = true;
    private NewsService newsService;
    private ArrayList<Article> articleList;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public NewsService() {
        newsService = this;
    }
    public class ServiceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            AsyncArticleTask asyncArticleLoader = new AsyncArticleTask(newsService);
            asyncArticleLoader.execute(intent.getStringExtra("SOURCE"));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        articleList = new ArrayList<Article>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                serviceReceiver = new ServiceReceiver();
                registerReceiver(serviceReceiver, new IntentFilter(MainActivity.REQUEST));

                while (running){
                    if (articleList.size() == 0 || articleList.size() != articleList.get(0).getTotal()){
                        try {
                            Thread.sleep(200);
                            continue;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent responseIntent = new Intent();
                        responseIntent.setAction(MainActivity.RESPONSE);
                        responseIntent.putExtra("articles", articleList);

                        sendBroadcast(responseIntent);
                        articleList.clear();
                    }
                }
            }
        }).start();
        return START_STICKY;
    }

    public void addArticle(Article article){
        articleList.add(article);
    }

}