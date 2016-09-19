package com.hellsayenci.t24.models;

import java.io.Serializable;

/**
 * Created by hellsayenci on 14/09/16.
 */
public class Stats implements Serializable {
    int likes,comments,shares,interactions,reads,pageviews;

    public int getLikes() {
        return likes;
    }

    public int getComments() {
        return comments;
    }

    public int getShares() {
        return shares;
    }

    public int getInteractions() {
        return interactions;
    }

    public int getReads() {
        return reads;
    }

    public int getPageviews() {
        return pageviews;
    }
}
