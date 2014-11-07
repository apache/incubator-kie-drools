package org.drools.compiler.xpath;

import java.util.ArrayList;
import java.util.List;

public class Adult extends Person {

    private final List<Child> children = new ArrayList<Child>();

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
