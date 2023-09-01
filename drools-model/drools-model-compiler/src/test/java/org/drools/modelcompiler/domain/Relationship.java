package org.drools.modelcompiler.domain;

import org.kie.api.definition.type.Position;

public class Relationship {

    @Position(0)
    private final String start;

    @Position(1)
    private final String end;

    public Relationship(String start, String end ) {
        this.start = start;
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }
}
