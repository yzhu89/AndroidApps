package com.example.newsgateway;

import android.os.Parcelable;

import java.util.ArrayList;

public class NewsSource {
    private String id;
    private String name;
    private String url;
    private String category;

    public NewsSource(String id, String name, String url, String category) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return name;
    }
}
