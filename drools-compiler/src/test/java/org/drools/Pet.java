package org.drools;

import java.util.HashMap;

public class Pet {

    HashMap attributes =new HashMap();

    String ownerName;

    public Pet(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public HashMap getAttributes() {
        return attributes;
    }
}
