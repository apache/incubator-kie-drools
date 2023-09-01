package org.drools.mvel.integrationtests.facts;

public class RootFact {

    // Intentionally int
    private final int id;

    public RootFact(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
