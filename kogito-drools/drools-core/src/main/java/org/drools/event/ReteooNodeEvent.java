package org.drools.event;

import org.drools.WorkingMemory;
import org.drools.spi.ReteooNode;
import org.drools.spi.Tuple;

public class ReteooNodeEvent {
    private final WorkingMemory workingMemory;

    private final ReteooNode    node;

    private final Tuple         tuple;

    private final boolean       passed;

    public ReteooNodeEvent(WorkingMemory workingMemory,
                           ReteooNode node,
                           Tuple tuple,
                           boolean passed){
        this.workingMemory = workingMemory;
        this.node = node;
        this.tuple = tuple;
        this.passed = passed;
    }

    public ReteooNode getNode(){
        return this.node;
    }

    public boolean isPassed(){
        return this.passed;
    }

    public Tuple getTuple(){
        return this.tuple;
    }

    public WorkingMemory getWorkingMemory(){
        return this.workingMemory;
    }

}
