package org.drools.core.beliefsystem.simple;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.common.TruthMaintenanceSystem.LogicalCallback;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;

/**
 * Default implementation emulates classical Drools TMS behaviour.
 *
 */
public class SimpleBeliefSystem
        implements
        BeliefSystem {
    private NamedEntryPoint        ep;
    private TruthMaintenanceSystem tms;

    public SimpleBeliefSystem(NamedEntryPoint ep,
                              TruthMaintenanceSystem tms) {
        super();
        this.ep = ep;
        this.tms = tms;
    }

    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return this.tms;
    }

    public void insert(LogicalDependency node,
                       BeliefSet beliefSet,
                       PropagationContext context,
                       ObjectTypeConf typeConf) {
        boolean empty = beliefSet.isEmpty();

        beliefSet.add( node.getJustifierEntry() );

        if ( empty ) {
            InternalFactHandle handle = beliefSet.getFactHandle();

            ep.insert( handle,
                       handle.getObject(),
                       node.getJustifier().getRule(),
                       node.getJustifier(),
                       typeConf,
                       null );
        }
    }

    public void read(LogicalDependency node,
                     BeliefSet beliefSet,
                     PropagationContext context,
                     ObjectTypeConf typeConf) {
        //insert(node, beliefSet, context, typeConf );
        beliefSet.add( node.getJustifierEntry() );
    }

    public void delete(LogicalDependency node,
                       BeliefSet beliefSet,
                       PropagationContext context) {
        SimpleBeliefSet sBeliefSet = (SimpleBeliefSet) beliefSet;
        beliefSet.remove( node.getJustifierEntry() );

        InternalFactHandle bfh = beliefSet.getFactHandle();
        
        if ( beliefSet.isEmpty() &&
             !((context.getType() == PropagationContext.DELETION || context.getType() == PropagationContext.MODIFICATION) // retract and modifies clean up themselves
             &&
             context.getFactHandle() == bfh) ) {
            
            if ( sBeliefSet.getWorkingMemoryAction() == null ) {
                WorkingMemoryAction action = new LogicalCallback( bfh,
                                                                  context,
                                                                  node.getJustifier(),
                                                                  false,
                                                                  true );
                ep.enQueueWorkingMemoryAction( action );
                sBeliefSet.setWorkingMemoryAction( action );
            } else {
                // was previous scheduled, so make sure it's a full retract and not an update
                LogicalCallback callback = ( LogicalCallback  ) sBeliefSet.getWorkingMemoryAction();
                callback.setUpdate( false );
                callback.setFullyRetract( true );
            }
            
        } else if ( !beliefSet.isEmpty() && beliefSet.getFactHandle().getObject() == node.getObject() ) {
            // prime has changed, to update new object                      
           // Equality might have changed on the object, so remove (which uses the handle id) and add back in
           ((NamedEntryPoint)bfh.getEntryPoint()).getObjectStore().updateHandle( bfh,  ((LinkedListEntry<LogicalDependency>) beliefSet.getFirst()).getObject().getObject() );

            if ( sBeliefSet.getWorkingMemoryAction() == null ) {
                // Only schedule if we don't already have one scheduled
                WorkingMemoryAction action = new LogicalCallback( bfh,
                                                                  context,
                                                                  node.getJustifier(),
                                                                  true,
                                                                  false );
                ep.enQueueWorkingMemoryAction( action );
                sBeliefSet.setWorkingMemoryAction( action );
            }
        }

        //        if ( beliefSet.size() > 1 && beliefSet.getFactHandle().getObject() == node.getObject() ) {
        //            // The "Prime" Object no longer has a backer, so it needs to be updated
        //            beliefSet.remove( node.getJustifierEntry() );
        //            
        //            if ( beliefSet.isEmpty() ) {
        //                WorkingMemoryAction action = new LogicalCallback( tms,
        //                                                                         node,
        //                                                                         beliefSet,
        //                                                                         context,
        //                                                                         node.getJustifier() );
        //                ep.queueWorkingMemoryAction( action );            
        //            }             
        //        } else {
        //            beliefSet.remove( node.getJustifierEntry() );
        //            
        //            if ( beliefSet.isEmpty() ) {
        //                WorkingMemoryAction action = new LogicalCallback( tms,
        //                                                                         node,
        //                                                                         beliefSet,
        //                                                                         context,
        //                                                                         node.getJustifier() );
        //                ep.queueWorkingMemoryAction( action );            
        //            }            
        //        }        
    }

    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new SimpleBeliefSet( this, fh );
    }

    public LogicalDependency newLogicalDependency(Activation activation,
                                                  BeliefSet beliefSet,
                                                  Object object,
                                                  Object value) {
        return new SimpleLogicalDependency( activation, beliefSet, object, value );
    }
}
