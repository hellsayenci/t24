package com.hellsayenci.t24.models;

import java.io.Serializable;

/**
 * Created by hellsayenci on 14/09/16.
 */
public class Images implements Serializable {
    String list,box,page,grid;

    public String getList() {
        return list;
    }

    public String getBox() {
        return box;
    }

    public String getPage() {
        return page;
    }

    public String getGrid() {
        return grid;
    }
}
