package org.drools.compiler.xpath;

import org.drools.core.phreak.ReactiveObject;

public class Toy extends ReactiveObject {

    private final String name;

    public Toy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
