package org.drools.runtime.rule.impl;

import org.drools.base.InternalViewChangedEventListener;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.Rule;
import org.drools.runtime.rule.ViewChangedEventListener;
import org.drools.spi.PropagationContext;

public class OpenQueryViewChangedEventListenerAdapter
    implements
    InternalViewChangedEventListener {

    private ViewChangedEventListener viewEventListener;
    
    public OpenQueryViewChangedEventListenerAdapter(ViewChangedEventListener viewEventListener) {
        this.viewEventListener = viewEventListener;
    }

    public void rowAdded(final Rule rule,
                         final LeftTuple leftTuple,
                         final PropagationContext context,
                         final InternalWorkingMemory workingMemory) {
        RowAdapter rowAdapter = new RowAdapter(rule,
                                               leftTuple);
        leftTuple.setObject( rowAdapter );
        this.viewEventListener.rowAdded( rowAdapter );
    }

    public void rowRemoved(final Rule rule,
                           final LeftTuple leftTuple,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        RowAdapter rowAdapter = (RowAdapter) leftTuple.getObject();
        this.viewEventListener.rowRemoved( rowAdapter );
    }

    public void rowUpdated(final Rule rule,
                           final LeftTuple leftTuple,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        RowAdapter rowAdapter = (RowAdapter) leftTuple.getObject();
        this.viewEventListener.rowUpdated( rowAdapter );
    }

}
