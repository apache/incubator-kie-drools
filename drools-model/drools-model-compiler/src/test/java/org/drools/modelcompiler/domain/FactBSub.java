package org.drools.modelcompiler.domain;

public class FactBSub extends FactASuper {

    private String name;

    public FactBSub() {}

    public FactBSub(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
