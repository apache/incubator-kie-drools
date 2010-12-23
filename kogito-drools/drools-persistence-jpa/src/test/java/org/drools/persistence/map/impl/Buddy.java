package org.drools.persistence.map.impl;

import java.io.Serializable;

public class Buddy implements Serializable{

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
