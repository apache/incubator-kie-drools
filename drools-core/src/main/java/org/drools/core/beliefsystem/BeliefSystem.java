package org.drools.core.beliefsystem;

import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.kie.internal.runtime.beliefs.Mode;

public interface BeliefSystem<M extends ModedAssertion<M>> {
    
    /**
     * TypeConf is already available, so we pass it, to avoid additional lookups
     * @param node
     * @param beliefSet
     * @param context
     * @param typeConf
     */
    public BeliefSet<M> insert(LogicalDependency<M> node,
                               BeliefSet<M> beliefSet,
                               PropagationContext context,
                               ObjectTypeConf typeConf);

    /**
     *
     * @param mode
     * @param rule
     * @param activation
     * @param beliefSet
     * @param context
     * @param typeConf
     * @return
     */
    public BeliefSet<M> insert( M mode,
                                RuleImpl rule,
                                Activation activation,
                                Object payload,
                                BeliefSet<M> beliefSet,
                                PropagationContext context,
                                ObjectTypeConf typeConf);

    /**
     * The typeConf has not yet been looked up, so we leave it to the implementation to decide if it needs it or not.
     * @param node
     * @param beliefSet
     * @param context
     */
    public void delete(LogicalDependency<M> node,
                       BeliefSet<M> beliefSet,
                       PropagationContext context);
    
    public void delete(M mode,
                       RuleImpl rule,
                       Activation activation,
                       Object payload,
                       BeliefSet<M> beliefSet,
                       PropagationContext context);

    public BeliefSet newBeliefSet(InternalFactHandle fh);
    
    public LogicalDependency newLogicalDependency(final Activation<M> activation,
                                                  final BeliefSet<M> beliefSet,
                                                  final Object object, 
                                                  final Object value);

    public void read(LogicalDependency<M> node,
                     BeliefSet<M> beliefSet,
                     PropagationContext context,
                     ObjectTypeConf typeConf);

    public void stage(PropagationContext context,
                      BeliefSet<M> beliefSet);

    public void unstage(PropagationContext context,
                        BeliefSet<M> beliefSet);
    
    public TruthMaintenanceSystem getTruthMaintenanceSystem();

    public M asMode( Object value );
}
