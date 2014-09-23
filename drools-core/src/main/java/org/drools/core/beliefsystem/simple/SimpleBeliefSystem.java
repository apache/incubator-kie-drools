package org.drools.core.beliefsystem.simple;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
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
        BeliefSystem<SimpleMode> {
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

    public void delete(LogicalDependency<SimpleMode> node,
                       BeliefSet<SimpleMode> beliefSet,
                       PropagationContext context) {
        SimpleBeliefSet sBeliefSet = (SimpleBeliefSet) beliefSet;
        beliefSet.remove( node.getMode() );

        InternalFactHandle bfh = beliefSet.getFactHandle();
        
        if ( beliefSet.isEmpty() && bfh.getEqualityKey().getStatus() != EqualityKey.STATED &&
             !((context.getType() == PropagationContext.DELETION || context.getType() == PropagationContext.MODIFICATION) // retract and modifies clean up themselves
             &&
             context.getFactHandle() == bfh) ) {

            ((NamedEntryPoint) bfh.getEntryPoint()).delete( bfh, context.getRuleOrigin(), node.getJustifier());

        } else if ( !beliefSet.isEmpty() && beliefSet.getFactHandle().getObject() == node.getObject() && node.getObject() != bfh.getObject() ) {
            // prime has changed, to update new object
            // Equality might have changed on the object, so remove (which uses the handle id) and add back in
            ((NamedEntryPoint)bfh.getEntryPoint()).getObjectStore().updateHandle(bfh, ((SimpleMode) beliefSet.getFirst()).getObject().getObject());

            ((NamedEntryPoint) bfh.getEntryPoint() ).update( bfh, true, bfh.getObject(), Long.MAX_VALUE, Object.class, null );
        }
    }

    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new SimpleBeliefSet( this, fh );
    }

    public LogicalDependency newLogicalDependency(Activation activation,
                                                  BeliefSet beliefSet,
                                                  Object object,
                                                  Object value) {
        SimpleMode mpde = new SimpleMode();
        SimpleLogicalDependency dep =  new SimpleLogicalDependency( activation, beliefSet, object, mpde );
        mpde.setObject( dep );
        return dep;
    }

}
