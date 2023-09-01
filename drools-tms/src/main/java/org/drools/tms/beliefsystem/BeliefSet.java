package org.drools.tms.beliefsystem;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.util.FastIterator;
import org.drools.core.common.PropagationContext;

public interface BeliefSet<M extends ModedAssertion<M>> {
    BeliefSystem getBeliefSystem();
    
    InternalFactHandle getFactHandle();
    
    M getFirst();

    FastIterator iterator();

    void add(M node);
    void remove(M node);
    
    boolean isEmpty();
    int size();

    /**
     *  This will remove all entries and do clean up, like retract FHs.
     * @param propagationContext
     */
    void cancel(final PropagationContext propagationContext);

    /**
     * This will remove all entries, but not do cleanup, the FH is most likely needed else where
     * @param propagationContext
     */
    void clear(PropagationContext propagationContext);
    
    void setWorkingMemoryAction(WorkingMemoryAction wmAction);

    boolean isNegated();

    boolean isDecided();

    boolean isConflicting();

    boolean isPositive();

}
