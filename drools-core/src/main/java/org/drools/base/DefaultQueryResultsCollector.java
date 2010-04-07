package org.drools.base;

import java.util.ArrayList;
import java.util.List;

import org.drools.common.DisconnectedFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.Query;
import org.drools.rule.Variable;
import org.drools.spi.PropagationContext;

public class DefaultQueryResultsCollector
    implements
    QueryResultCollector {

    private List results;

    public DefaultQueryResultsCollector() {
        this.results = new ArrayList( 250 );
    }

    public List getResults() {
        return this.results;
    }

    public void add(final LeftTuple tuple,
                    final PropagationContext context,
                    final InternalWorkingMemory workingMemory) {
        InternalFactHandle[] handles = new InternalFactHandle[tuple.getIndex() + 1];
        LeftTuple entry = tuple;

        // Add all the FactHandles except the root DroolQuery object
        while ( entry.getIndex() > 0 ) {
            InternalFactHandle handle = entry.getLastHandle();
            handles[entry.getIndex()] = new DisconnectedFactHandle( handle.getId(),
                                                                    handle.getIdentityHashCode(),
                                                                    handle.getObjectHashCode(),
                                                                    handle.getRecency(),
                                                                    handle.getObject() );
            entry = entry.getParent();
        }

        // Get the Query object
        InternalFactHandle handle = entry.getLastHandle();
        DroolsQuery query = (DroolsQuery) handle.getObject();

        // Copy of it's arguments for unification variables.
        Object[] args = query.getArguments();
        Object[] newArgs = new Object[args.length];
        for ( int i = 0, length = args.length; i < length; i++ ) {
            if ( args[i] instanceof Variable ) {
                newArgs[i] = ((Variable) args[i]).getValue();
            } else {
                newArgs[i] = args[i];
            }
        }
        handles[entry.getIndex()] = new DisconnectedFactHandle( handle.getId(),
                                                                handle.getIdentityHashCode(),
                                                                handle.getObjectHashCode(),
                                                                handle.getRecency(),
                                                                new Arguments( newArgs ) );

        this.results.add( handles );

    }

}