package com.hellsayenci.t24.models;

import java.io.Serializable;

/**
 * Created by hellsayenci on 14/09/16.
 */
public class StoriesResponse implements Serializable {
    Story[] data;
    Paging paging;
    boolean result;

    public Story[] getData() {
        return data;
    }

    public Paging getPaging() {
        return paging;
    }

    public boolean isResult() {
        return result;
    }
}
