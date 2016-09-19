package com.hellsayenci.t24.models;

import java.io.Serializable;

/**
 * Created by hellsayenci on 14/09/16.
 */
public class Category implements Serializable {
    int id;
    String name,alias;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

}
