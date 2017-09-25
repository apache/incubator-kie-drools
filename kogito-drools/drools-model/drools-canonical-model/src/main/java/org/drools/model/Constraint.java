package org.drools.model;

import java.util.List;

public interface Constraint {
    enum Type { SINGLE, OR, AND }

    List<Constraint> getChildren();

    Type getType();
}
