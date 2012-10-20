package org.drools.common;

import org.drools.FactException;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;

public class TruthMaintenanceSystemHelper {
    /**
     * The Prime FactHandle is being removed from the system so remove any logical dependencies
     * between the  justified FactHandle and its justifiers. It then iterates over all the LogicalDependency nodes, if any,
     * in the returned Set and removes the LogicalDependency node from the LinkedList maintained
     * by the Activation.
     *
     * @see LogicalDependency
     *
     * @param handle - The FactHandle to be removed
     * @throws FactException
     */
    public static void removeLogicalDependencies(final InternalFactHandle handle) throws FactException {
        final BeliefSet beliefSet = handle.getEqualityKey().getBeliefSet();

        if ( beliefSet != null && !beliefSet.isEmpty() ) {
            for ( LinkedListEntry entry = (LinkedListEntry) beliefSet.getFirst(); entry != null; entry = (LinkedListEntry) entry.getNext() ) {
                final LogicalDependency node = (LogicalDependency) entry.getObject();
                node.getJustifier().getLogicalDependencies().remove( node );
            }
        }
        
        beliefSet.clear();
        handle.getEqualityKey().setBeliefSet( null );
    
    }
    
    
    /**
     * An Activation is no longer true so it no longer justifies  any  of the logical facts
     * it logically  asserted. It iterates  over the Activation's LinkedList of DependencyNodes
     * it retrieves the justitication  set for each  DependencyNode's FactHandle and  removes
     * itself. If the Set is empty it retracts the FactHandle from the WorkingMemory.
     *
     * @param activation
     * @param context
     * @param rule
     * @throws FactException
     */
    public static void removeLogicalDependencies(final Activation activation,
                                                 final PropagationContext context,
                                                 final Rule rule) throws FactException {
        final LinkedList<LogicalDependency> list = activation.getLogicalDependencies();
        if ( list == null || list.isEmpty() ) {
            return;
        }

        for ( LogicalDependency node = list.getFirst(); node != null; node = node.getNext() ) {
            removeLogicalDependency( activation, node, context );
        }
    }

    public static void removeLogicalDependency(final Activation activation,
                                               final LogicalDependency node,
                                               final PropagationContext context) {
        final BeliefSet beliefSet = ( BeliefSet ) node.getJustified();

        beliefSet.getBeliefSystem().delete( node, beliefSet, context );

    }
}
