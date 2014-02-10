package org.drools.core.beliefsystem.defeasible;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSystem;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.rule.Rule;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.LinkedList;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefeasibleBeliefSystem extends JTMSBeliefSystem  {

    public DefeasibleBeliefSystem(NamedEntryPoint ep, TruthMaintenanceSystem tms) {
        super(ep, tms);
    }

    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new DefeasibleBeliefSet(this, fh);
    }

    public LogicalDependency newLogicalDependency(Activation activation, BeliefSet beliefSet, Object object, Object value) {
        return new DefeasibleLogicalDependency(activation, beliefSet, object, value);
    }


    public void insert(LogicalDependency node,
                       BeliefSet beliefSet,
                       PropagationContext context,
                       ObjectTypeConf typeConf) {
        boolean wasEmpty = beliefSet.isEmpty();
        boolean wasNegated = beliefSet.isNegated();
        boolean wasUndecided = beliefSet.isUndecided();

        super.insert( node, beliefSet, context, typeConf );

        if ( ! wasEmpty && ! wasUndecided
             && ! beliefSet.isUndecided() && ! beliefSet.isEmpty() ) {

            DefeasibleBeliefSet dbs = (DefeasibleBeliefSet) beliefSet;

            if ( ! wasNegated && beliefSet.isNegated() ) {
                InternalFactHandle fh = dbs.getPositiveFactHandle();
                NamedEntryPoint pep =  ((NamedEntryPoint) fh.getEntryPoint());
                pep.getEntryPointNode().retractObject( fh, context, typeConf, pep.getInternalWorkingMemory() );

                insertBelief( node, typeConf, dbs, context, wasEmpty, wasNegated, false );
            } else if ( wasNegated && ! beliefSet.isNegated() ) {
                InternalFactHandle fh = dbs.getNegativeFactHandle();
                ((NamedEntryPoint) fh.getEntryPoint()).delete( fh, context.getRuleOrigin(), node.getJustifier() );

                insertBelief( node, typeConf, dbs, context, wasEmpty, wasNegated, false );
            }
        }

    }

}
