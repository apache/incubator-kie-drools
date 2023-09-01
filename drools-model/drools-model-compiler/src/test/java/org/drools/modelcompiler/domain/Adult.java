package org.drools.modelcompiler.domain;

import java.util.List;

import org.drools.core.phreak.ReactiveList;

public class Adult extends Person {

    private final List<Child> children = new ReactiveList<Child>();
    private Person[] childrenA = new Person[0];

    public Adult(String name, int age) {
        super(name, age);
    }

    public List<Child> getChildren() {
        return children;
    }

    public void addChild(Child child) {
        children.add(child);
    }


    public Person[] getChildrenA() {
        return childrenA;
    }

    public void setChildrenA(Person[] children) {
        this.childrenA = children;
    }
}

