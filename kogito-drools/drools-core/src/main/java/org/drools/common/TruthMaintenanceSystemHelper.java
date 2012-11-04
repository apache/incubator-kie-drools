package org.drools.common;

import org.drools.FactException;
import org.drools.beliefsystem.BeliefSet;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;

public class TruthMaintenanceSystemHelper {

    public static void removeLogicalDependencies(final InternalFactHandle handle, final PropagationContext propagationContext ) throws FactException {
        final BeliefSet beliefSet = handle.getEqualityKey().getBeliefSet();
        if ( beliefSet != null && !beliefSet.isEmpty() ) {
            beliefSet.cancel(propagationContext);
        }
    }
    
    public static void clearLogicalDependencies(final InternalFactHandle handle, final PropagationContext propagationContext ) throws FactException {
        final BeliefSet beliefSet = handle.getEqualityKey().getBeliefSet();
        if ( beliefSet != null && !beliefSet.isEmpty() ) {
            beliefSet.clear(propagationContext);
        }
    }    
    
    
    public static void removeLogicalDependencies(final Activation activation,
                                                 final PropagationContext context,
                                                 final Rule rule) throws FactException {
        final LinkedList<LogicalDependency> list = activation.getLogicalDependencies();
        if ( list == null || list.isEmpty() ) {
            return;
        }

        for ( LogicalDependency node = list.getFirst(); node != null; node = node.getNext() ) {
            removeLogicalDependency( node, context );
        }
        activation.setLogicalDependencies( null );
    }

    public static void removeLogicalDependency(final LogicalDependency node,
                                               final PropagationContext context) {
        final BeliefSet beliefSet = ( BeliefSet ) node.getJustified();
        beliefSet.getBeliefSystem().delete( node, beliefSet, context );
    }
}
