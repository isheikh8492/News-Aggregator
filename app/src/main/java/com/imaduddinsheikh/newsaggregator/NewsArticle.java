package com.imaduddinsheikh.newsaggregator;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Arrays;

public class NewsArticle implements Serializable {
    private final String author;
    private final String title;
    private final String description;
    private final String url;
    private final String imageUrl;
    private final String publishDate;

    private final String[] source;

    public NewsArticle(String author, String title, String description, String url, String imageUrl, String publishDate, String[] source) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
        this.publishDate = publishDate;
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String[] getSource() {
        return source;
    }

    @NonNull
    @Override
    public String toString() {
        return "NewsArticle{" +
                "author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", sourceId='" + Arrays.toString(source) + '\'' +
                '}';
    }
}
