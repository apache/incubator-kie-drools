package org.drools.common;

import org.drools.FactHandle;
import org.drools.common.TruthMaintenanceSystem.LogicalRetractCallback;
import org.drools.core.util.LinkedList;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.rule.Rule;
import org.drools.spi.PropagationContext;

/**
 * Default implementation emulates classical Drools TMS behaviour.
 *
 */
public class SimpleBeliefSystem
        implements
        BeliefSystem {
    private InternalWorkingMemory  wm;
    private TruthMaintenanceSystem tms;

    public SimpleBeliefSystem(InternalWorkingMemory wm,
                              TruthMaintenanceSystem tms) {
        super();
        this.wm = wm;
        this.tms = tms;
    }

    public void insert(LogicalDependency node,
                       BeliefSet beliefSet,
                       PropagationContext context,
                       ObjectTypeConf typeConf) {
        boolean empty = beliefSet.isEmpty();
               
        beliefSet.add( node.getJustifierEntry() );
        
        if ( empty) {
            InternalFactHandle handle = (InternalFactHandle) node.getJustified();            
            ((NamedEntryPoint)handle.getEntryPoint()).insert( handle,
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
        beliefSet.add( node.getJustifierEntry() );
    }

    public void delete(LogicalDependency node,
                       BeliefSet beliefSet,
                       PropagationContext context) {
        beliefSet.remove( node.getJustifierEntry() );
        
        WorkingMemoryAction action = new LogicalRetractCallback( tms,
                                                                 node,
                                                                 beliefSet,
                                                                 (InternalFactHandle) node.getJustified(),
                                                                 context,
                                                                 node.getJustifier() );
        wm.queueWorkingMemoryAction( action );
    }

    public BeliefSet newBeliefSet() {
        return new SimpleBeliefSet();
    }


}
