package org.drools.core.beliefsystem;

import org.drools.common.InternalFactHandle;
import org.drools.common.WorkingMemoryAction;
import org.drools.core.util.LinkedListNode;
import org.drools.spi.PropagationContext;

public interface BeliefSet {
    public BeliefSystem getBeliefSystem();
    
    public InternalFactHandle getFactHandle();
    
    public LinkedListNode getFirst();
    
    public void add(LinkedListNode node);
    public void remove(LinkedListNode node);
    
    public boolean isEmpty();
    //public boolean isPropagated();
    public int size();

    /**
     *  This will remove all entries and do clean up, like retract FHs.
     * @param propagationContext
     */
    public void cancel(final PropagationContext propagationContext);

    /**
     * This will remove all entries, but not do cleanup, the FH is most likely needed else where
     * @param propagationContext
     */
    public void clear(PropagationContext propagationContext);
    
    public void setWorkingMemoryAction(WorkingMemoryAction wmAction);


}
