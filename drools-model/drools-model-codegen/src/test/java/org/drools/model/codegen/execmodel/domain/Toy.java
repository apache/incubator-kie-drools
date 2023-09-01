package org.drools.model.codegen.execmodel.domain;

import org.drools.core.phreak.AbstractReactiveObject;

public class Toy extends AbstractReactiveObject {

    private String name;

    private String owner;

    private Integer targetAge;

    public Toy(String name) {
        this.name = name;
    }

    public Toy(String name, Integer targetAge) {
        this.name = name;
        this.targetAge = targetAge;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
        notifyModification();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getTargetAge() {
        return targetAge;
    }

    public void setTargetAge(Integer targetAge) {
        this.targetAge = targetAge;
    }

    @Override
    public String toString() {
        return "Toy: " + name;
    }
}

