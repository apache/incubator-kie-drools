package org.drools.beliefs.bayes;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.simple.SimpleBeliefSet;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.LinkedListEntry;

public class BayesBeliefSystem implements BeliefSystem {
    private NamedEntryPoint        ep;
    private TruthMaintenanceSystem tms;

    public BayesBeliefSystem(NamedEntryPoint ep,
                             TruthMaintenanceSystem tms) {
        super();
        this.ep = ep;
        this.tms = tms;
    }

    @Override
    public void insert(LogicalDependency node, BeliefSet beliefSet, PropagationContext context, ObjectTypeConf typeConf) {
        boolean wasEmpty = beliefSet.isEmpty();
        boolean wasUndecided = beliefSet.isUndecided();

        beliefSet.add(node.getJustifierEntry());

        PropertyReference evidence = (PropertyReference) beliefSet.getFactHandle().getObject();
        if (wasEmpty) {
            InternalFactHandle handle = beliefSet.getFactHandle();


//            ep.insert(handle,
//                      handle.getObject(),
//                      node.getJustifier().getRule(),
//                      node.getJustifier(),
//                      typeConf,
//                       null );
        } else if ( !wasUndecided && beliefSet.isUndecided() ) {
            // was decided, and now is not, so delete
        }
    }

    @Override
    public void delete(LogicalDependency node, BeliefSet beliefSet, PropagationContext context) {
        SimpleBeliefSet sBeliefSet = (SimpleBeliefSet) beliefSet;
        beliefSet.remove( node.getJustifierEntry() );

        InternalFactHandle bfh = beliefSet.getFactHandle();

        if ( beliefSet.isEmpty() && bfh.getEqualityKey().getStatus() != EqualityKey.STATED &&
             !((context.getType() == PropagationContext.DELETION || context.getType() == PropagationContext.MODIFICATION) // retract and modifies clean up themselves
               &&
               context.getFactHandle() == bfh) ) {

            ((NamedEntryPoint) bfh.getEntryPoint()).delete( bfh, context.getRuleOrigin(), node.getJustifier());

        } else if ( !beliefSet.isEmpty() && beliefSet.getFactHandle().getObject() == node.getObject() && node.getObject() != bfh.getObject() ) {
            // prime has changed, to update new object
            // Equality might have changed on the object, so remove (which uses the handle id) and add back in
            ((NamedEntryPoint)bfh.getEntryPoint()).getObjectStore().updateHandle( bfh,  ((LinkedListEntry<LogicalDependency>) beliefSet.getFirst()).getObject().getObject() );

            ((NamedEntryPoint) bfh.getEntryPoint() ).update( bfh, true, bfh.getObject(), Long.MAX_VALUE, Object.class, null );
        }
    }

    @Override
    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return null;
    }

    @Override
    public LogicalDependency newLogicalDependency(Activation activation, BeliefSet beliefSet, Object object, Object value) {
        return new BayesLogicalDependency(activation, beliefSet, object, value);
    }

    @Override
    public void read(LogicalDependency node, BeliefSet beliefSet, PropagationContext context, ObjectTypeConf typeConf) {

    }

    @Override
    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return null;
    }

}
