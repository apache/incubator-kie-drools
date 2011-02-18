package org.drools;

import java.io.Serializable;

/**
 * A grand parent class
 */
public class GrandParent implements Serializable {
    private String name;

    public GrandParent() {
    }

    public GrandParent(final String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

}
