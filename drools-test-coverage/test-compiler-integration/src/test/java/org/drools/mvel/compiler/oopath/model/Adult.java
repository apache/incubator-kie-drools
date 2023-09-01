package org.drools.mvel.compiler.oopath.model;

import org.drools.core.phreak.ReactiveList;

import java.util.List;

public class Adult extends Person {

    private final List<Child> children = new ReactiveList<Child>();

    public Adult(String name, int age) {
        super(name, age);
    }

    public List<Child> getChildren() {
        return children;
    }

    public void addChild(Child child) {
        children.add(child);
    }
}
