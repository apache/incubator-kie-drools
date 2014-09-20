package org.drools.core.beliefsystem.simple;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.spi.PropagationContext;

public class SimpleBeliefSet extends LinkedList implements BeliefSet {
    private BeliefSystem beliefSystem;
    
    private InternalFactHandle fh;
    
    private WorkingMemoryAction wmAction;
    
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

    public void cancel(PropagationContext context) {        
        // get all but last, as that we'll do via the BeliefSystem, for cleanup
        for ( LinkedListEntry entry = (LinkedListEntry) getFirst(); entry != getLast();  ) {
            LinkedListEntry temp = (LinkedListEntry) entry.getNext(); // get next, as we are about to remove it
            final LogicalDependency node = (LogicalDependency) entry.getObject();
            node.getJustifier().getLogicalDependencies().remove( node );
            remove( entry );
            entry = temp;
        }
        
        LinkedListEntry last = (LinkedListEntry) getFirst();
        final LogicalDependency node = (LogicalDependency) last.getObject();
        node.getJustifier().getLogicalDependencies().remove( node );
        beliefSystem.delete( node, this, context );
    }
    
    public void clear(PropagationContext context) { 
        // remove all, but don't allow the BeliefSystem to clean up, the FH is most likely going to be used else where
        for ( LinkedListEntry entry = (LinkedListEntry) getFirst(); entry != null;  ) {
            LinkedListEntry temp = (LinkedListEntry) entry.getNext(); // get next, as we are about to remove it
            final LogicalDependency node = (LogicalDependency) entry.getObject();
            node.getJustifier().getLogicalDependencies().remove( node );
            remove( entry );
            entry = temp;
        }
    }    

    public WorkingMemoryAction getWorkingMemoryAction() {
        return wmAction;
    }

    public void setWorkingMemoryAction(WorkingMemoryAction wmAction) {
        this.wmAction = wmAction;
    }

    @Override
    public boolean isNegated() {
        return false;
    }

    @Override
    public boolean isUndecided() {
        return false;
    }

    @Override
    public boolean isConflicting() {
        return false;
    }

    @Override
    public boolean isPositive() {
        return ! isEmpty();
    }


}
