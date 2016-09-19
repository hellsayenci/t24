package com.hellsayenci.t24.models;

import java.io.Serializable;

/**
 * Created by hellsayenci on 14/09/16.
 */
public class Paging implements Serializable {
    int current,limit,pages,items;

    public int getCurrent() {
        return current;
    }

    public int getLimit() {
        return limit;
    }

    public int getPages() {
        return pages;
    }

    public int getItems() {
        return items;
    }
}
