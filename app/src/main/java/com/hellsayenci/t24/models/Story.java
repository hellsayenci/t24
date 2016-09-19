package com.hellsayenci.t24.models;

import java.io.Serializable;

/**
 * Created by hellsayenci on 14/09/16.
 */
public class Story implements Serializable {
    int id;
    String title,excerpt,alias,publishingDate;
    Urls urls;
    Category category;
    Stats stats;
    Images images;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getAlias() {
        return alias;
    }

    public String getPublishingDate() {
        return publishingDate;
    }

    public Urls getUrls() {
        return urls;
    }

    public Category getCategory() {
        return category;
    }

    public Stats getStats() {
        return stats;
    }

    public Images getImages() {
        return images;
    }
}
