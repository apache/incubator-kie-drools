package org.drools.model;

public interface WindowDefinition {

    enum Type { LENGTH, TIME }

    Type getType();

    long getValue();
}
