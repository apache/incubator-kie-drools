package org.drools.common;

import org.drools.FactHandle;
import org.drools.common.TruthMaintenanceSystem.LogicalRetractCallback;
import org.drools.core.util.LinkedList;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;

/**
 * Default implementation emulates classical Drools TMS behaviour.
 *
 */
public class SimpleBeliefSystem
        implements
        BeliefSystem {
    private NamedEntryPoint  ep;
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
        
        if ( empty) {
            InternalFactHandle handle = beliefSet.getFactHandle();
            
            ep.insert( handle,
                       handle.getObject(),
                       node.getJustifier().getRule(),
                       node.getJustifier(),
                       typeConf );
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
        if ( beliefSet.size() > 1 && beliefSet.getFactHandle().getObject() == node.getObject() ) {
            // The "Prime" Object no longer has a backer, so it needs to be updated
            beliefSet.remove( node.getJustifierEntry() );
            
            if ( beliefSet.isEmpty() ) {
                WorkingMemoryAction action = new LogicalRetractCallback( tms,
                                                                         node,
                                                                         beliefSet,
                                                                         context,
                                                                         node.getJustifier() );
                ep.queueWorkingMemoryAction( action );            
            }             
        } else {
            beliefSet.remove( node.getJustifierEntry() );
            
            if ( beliefSet.isEmpty() ) {
                WorkingMemoryAction action = new LogicalRetractCallback( tms,
                                                                         node,
                                                                         beliefSet,
                                                                         context,
                                                                         node.getJustifier() );
                ep.queueWorkingMemoryAction( action );            
            }            
        }        
    }

    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new SimpleBeliefSet(this, fh);
    }

    public LogicalDependency newLogicalDependency(Activation activation,
                                                  BeliefSet beliefSet,
                                                  Object object,
                                                  Object value) {
        return new SimpleLogicalDependency( activation, beliefSet, object, value );
    }
}
