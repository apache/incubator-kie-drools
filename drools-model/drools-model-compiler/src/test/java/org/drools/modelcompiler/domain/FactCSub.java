package org.drools.modelcompiler.domain;

public class FactCSub extends FactASuper {

    private String name;

    public FactCSub() {}

    public FactCSub(String name) {
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
