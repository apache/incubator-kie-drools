package org.drools.modelcompiler.domain;

public class FactASuper {

    public FactASuper() {}

    public String getName() {
        throw new UnsupportedOperationException("getName is not supported on FactASuper");
    }

    public void setName(String name) {
        throw new UnsupportedOperationException("setName is not supported on FactASuper");
    }
}
