package org.drools.reteoo.beliefsystem.simple;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.simple.BeliefSystemLogicalCallback;
import org.drools.core.beliefsystem.simple.SimpleBeliefSet;
import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.LinkedListEntry;

/**
 * Default implementation emulates classical Drools TMS behaviour.
 *
 */
public class ReteSimpleBeliefSystem
        implements
        BeliefSystem<SimpleMode> {
    private NamedEntryPoint        ep;
    private TruthMaintenanceSystem tms;

    public ReteSimpleBeliefSystem(NamedEntryPoint ep,
                                  TruthMaintenanceSystem tms) {
        super();
        this.ep = ep;
        this.tms = tms;
    }

    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return this.tms;
    }

    public void insert(LogicalDependency<SimpleMode> node,
                       BeliefSet<SimpleMode> beliefSet,
                       PropagationContext context,
                       ObjectTypeConf typeConf) {
        boolean empty = beliefSet.isEmpty();

        beliefSet.add( node.getMode() );

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

    public void read(LogicalDependency<SimpleMode> node,
                     BeliefSet<SimpleMode> beliefSet,
                     PropagationContext context,
                     ObjectTypeConf typeConf) {
        //insert(node, beliefSet, context, typeConf );
        beliefSet.add( node.getMode() );
    }

    @Override
    public void stage( PropagationContext context, BeliefSet<SimpleMode> beliefSet ) {
        
    }

    @Override
    public void unstage( PropagationContext context, BeliefSet<SimpleMode> beliefSet ) {

    }

    public void delete(LogicalDependency<SimpleMode> node,
                       BeliefSet<SimpleMode> beliefSet,
                       PropagationContext context) {
        SimpleBeliefSet sBeliefSet = (SimpleBeliefSet) beliefSet;
        beliefSet.remove( node.getMode() );

        InternalFactHandle bfh = beliefSet.getFactHandle();
        
        if ( beliefSet.isEmpty() &&
             !((context.getType() == PropagationContext.DELETION || context.getType() == PropagationContext.MODIFICATION) // retract and modifies clean up themselves
             &&
             context.getFactHandle() == bfh) ) {
            
            if ( sBeliefSet.getWorkingMemoryAction() == null ) {
                WorkingMemoryAction action = new BeliefSystemLogicalCallback( bfh,
                                                                              context,
                                                                              node.getJustifier(),
                                                                              false,
                                                                              true );
                ep.enQueueWorkingMemoryAction( action );
                sBeliefSet.setWorkingMemoryAction( action );
            } else {
                // was previous scheduled, so make sure it's a full retract and not an update
                BeliefSystemLogicalCallback callback = ( BeliefSystemLogicalCallback  ) sBeliefSet.getWorkingMemoryAction();
                callback.setUpdate( false );
                callback.setFullyRetract( true );
            }
            
        } else if ( !beliefSet.isEmpty() && beliefSet.getFactHandle().getObject() == node.getObject() ) {
            // prime has changed, to update new object                      
           // Equality might have changed on the object, so remove (which uses the handle id) and add back in
           ((NamedEntryPoint)bfh.getEntryPoint()).getObjectStore().updateHandle( bfh,  ((SimpleMode) beliefSet.getFirst()).getObject().getObject() );

            if ( sBeliefSet.getWorkingMemoryAction() == null ) {
                // Only schedule if we don't already have one scheduled
                WorkingMemoryAction action = new BeliefSystemLogicalCallback( bfh,
                                                                              context,
                                                                              node.getJustifier(),
                                                                              true,
                                                                              false );
                ep.enQueueWorkingMemoryAction( action );
                sBeliefSet.setWorkingMemoryAction( action );
            }
        }
    }

    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new SimpleBeliefSet( this, fh );
    }

    public LogicalDependency newLogicalDependency(Activation<SimpleMode> activation,
                                                  BeliefSet<SimpleMode> beliefSet,
                                                  Object object,
                                                  Object value) {
        return new SimpleLogicalDependency( activation, beliefSet, object, null );
    }
}
