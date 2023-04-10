package com.imaduddinsheikh.newsaggregator;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class NewsSource implements Serializable {
    private final String id;
    private final String name;
    private final String category;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public NewsSource(String id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    @NonNull
    @Override
    public String toString() {
        return "NewsSource{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
