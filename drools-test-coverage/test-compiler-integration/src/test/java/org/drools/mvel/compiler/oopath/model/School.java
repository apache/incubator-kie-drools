package org.drools.mvel.compiler.oopath.model;

import java.util.List;

import org.drools.core.phreak.AbstractReactiveObject;
import org.drools.core.phreak.ReactiveList;

public class School extends AbstractReactiveObject {

    private final String name;

    private final List<Child> children = new ReactiveList<Child>();

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
