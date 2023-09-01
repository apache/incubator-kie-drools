package org.drools.mvel.compiler.oopath.model;


import org.drools.core.phreak.AbstractReactiveObject;

public class Disease extends AbstractReactiveObject {

    private String name;

    public Disease (final String name) {
        this.name = name;
    }

    public void setName(final String name ) {
        this.name = name;
        notifyModification();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ("Disease: " + name);
    }
}
