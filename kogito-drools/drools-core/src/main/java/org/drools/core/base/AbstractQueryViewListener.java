package org.drools.core.base;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.QueryTerminalNode;
import org.drools.core.spi.PropagationContext;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractQueryViewListener implements InternalViewChangedEventListener {

    protected List<Object> results;

    public AbstractQueryViewListener() {
        this.results = new ArrayList<Object>(250);
    }

    public List<? extends Object> getResults() {
        return this.results;
    }

    public abstract InternalFactHandle getHandle(InternalFactHandle originalHandle);

    public void rowAdded(final RuleImpl rule,
            final LeftTuple tuple,
            final PropagationContext context,
            final InternalWorkingMemory workingMemory) {
        InternalFactHandle[] handles = new InternalFactHandle[tuple.getIndex() + 1];
        LeftTuple entry = tuple;

        // Add all the FactHandles
        while ( entry != null ) {
            InternalFactHandle handle = entry.getFactHandle();
            if ( handle != null ) {
                handles[entry.getIndex()] = getHandle(handle);
            }
            entry = entry.getParent();
        }

        QueryTerminalNode node = ( QueryTerminalNode ) tuple.getTupleSink();
        this.results.add( new QueryRowWithSubruleIndex(handles, node.getSubruleIndex()) );
    }

    public void rowRemoved( final RuleImpl rule,
                            final LeftTuple tuple,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory ) {
    }

    public void rowUpdated( final RuleImpl rule,
                            final LeftTuple tuple,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory ) {
    }

}
