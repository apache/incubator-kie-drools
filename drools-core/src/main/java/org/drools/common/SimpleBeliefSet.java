package org.drools.common;

import org.drools.core.util.LinkedList;

public class SimpleBeliefSet extends LinkedList implements BeliefSet {
    private BeliefSystem beliefSystem;
    
    private InternalFactHandle fh;
    
    public SimpleBeliefSet(BeliefSystem beliefSystem, InternalFactHandle fh) {
        this.beliefSystem = beliefSystem;
        this.fh = fh;
    }
    
    public BeliefSystem getBeliefSystem() {
        return beliefSystem;
    }

    public InternalFactHandle getFactHandle() {
        return this.fh;
    }
    
}
