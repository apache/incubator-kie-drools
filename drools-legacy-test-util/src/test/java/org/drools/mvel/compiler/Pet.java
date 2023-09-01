package org.drools.mvel.compiler;

import java.util.HashMap;

public class Pet {

    HashMap attributes =new HashMap();

    String ownerName;

    public Pet() {

    }

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
