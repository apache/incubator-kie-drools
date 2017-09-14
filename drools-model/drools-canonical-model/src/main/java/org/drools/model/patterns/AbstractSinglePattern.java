package org.drools.model.patterns;

import org.drools.model.Condition;
import org.drools.model.Condition.Type;

public abstract class AbstractSinglePattern {

    public Condition.Type getType() {
        return Type.PATTERN;
    }
}
