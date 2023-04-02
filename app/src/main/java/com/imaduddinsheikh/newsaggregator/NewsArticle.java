package com.imaduddinsheikh.newsaggregator;

public class NewsArticle {
    private final String author;
    private final String title;
    private final String description;
    private final String url;
    private final String imageUrl;
    private final String publishDate;

    public NewsArticle(String author, String title, String description, String url, String imageUrl, String publishDate) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
        this.publishDate = publishDate;
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
}
