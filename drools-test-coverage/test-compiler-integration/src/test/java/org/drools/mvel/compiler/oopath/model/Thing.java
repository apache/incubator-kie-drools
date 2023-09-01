package org.drools.mvel.compiler.oopath.model;

import java.util.ArrayList;
import java.util.List;

public class Thing {
    private final String name;
    private final List<Thing> children = new ArrayList<Thing>();

    public Thing( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addChild(Thing child) {
        children.add(child);
    }

    public List<Thing> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return name;
    }
}
