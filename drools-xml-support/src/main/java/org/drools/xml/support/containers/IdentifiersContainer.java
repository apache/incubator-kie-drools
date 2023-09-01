package org.drools.xml.support.containers;

public class IdentifiersContainer {

    private String identifier;
    private int    index;

    public IdentifiersContainer() {

    }

    public IdentifiersContainer(String identifier,
                                int index) {
        this.identifier = identifier;
        this.index = index;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getIndex() {
        return index;
    }

}
