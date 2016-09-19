package com.hellsayenci.t24.models;

import java.io.Serializable;

/**
 * Created by hellsayenci on 16/09/16.
 */
public class StoryDetail implements Serializable {
    int id;
    String title,alias;
    Urls urls;
    Category category;
    Images images;
    String publishingDate, excerpt, text;
    Stats stats;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAlias() {
        return alias;
    }

    public Urls getUrls() {
        return urls;
    }

    public Category getCategory() {
        return category;
    }

    public Images getImages() {
        return images;
    }

    public String getPublishingDate() {
        return publishingDate;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getText() {
        return text;
    }

    public Stats getStats() {
        return stats;
    }
}
