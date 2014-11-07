package org.drools.compiler.xpath;

import org.drools.core.phreak.ReactiveObject;

import java.util.ArrayList;
import java.util.List;

public class School extends ReactiveObject {

    private final String name;

    private final List<Child> children = new ArrayList<Child>();

    public School(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void addChild(Child child) {
        children.add(child);
    }
}
