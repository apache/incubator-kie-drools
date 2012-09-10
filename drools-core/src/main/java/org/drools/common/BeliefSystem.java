package org.drools.common;

import org.drools.reteoo.ObjectTypeConf;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;

public interface BeliefSystem {
    
    /**
     * TypeConf is already available, so we pass it, to avoid additional lookups
     * @param node
     * @param beliefSet
     * @param context
     * @param typeConf
     */
    public void insert(LogicalDependency node, 
                       BeliefSet beliefSet,
                       PropagationContext context,
                       ObjectTypeConf typeConf);
    
    /**
     * The typeConf has not yet been looked up, so we leave it to the implementation to decide if it needs it or not.
     * @param node
     * @param beliefSet
     * @param context
     */
    public void delete(LogicalDependency node, 
                       BeliefSet beliefSet,
                       PropagationContext context);
    
    public BeliefSet newBeliefSet();
    
    public LogicalDependency newLogicalDependency(final Activation activation,
                                                  final InternalFactHandle handle,
                                                  final Object value);

    public void read(LogicalDependency node,
                     BeliefSet beliefSet,
                     PropagationContext context,
                     ObjectTypeConf typeConf);
}