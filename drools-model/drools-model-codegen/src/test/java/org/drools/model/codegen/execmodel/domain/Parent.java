package org.drools.model.codegen.execmodel.domain;

import java.util.StringJoiner;

public class Parent {

    private final String name;
    private final Child child;

    public Parent(String name, Child child) {
        this.name = name;
        this.child = child;
    }

    public String getName() {
        return name;
    }

    public Child getChild() {
        return child;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Parent.class.getSimpleName() + "[", "]")
                .add("child=" + child)
                .add("name='" + name + "'")
                .toString();
    }
}