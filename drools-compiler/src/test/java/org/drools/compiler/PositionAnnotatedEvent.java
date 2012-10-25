package org.drools.compiler;

import org.drools.definition.type.Position;

/**
 * Sample event annotated with @Position metadata.
 */
public class PositionAnnotatedEvent {

    @Position(1)
    private String arg1;

    @Position(0)
    private String arg0;

    public String getArg1() {
        return arg1;
    }

    public String getArg0() {
        return arg0;
    }
}
