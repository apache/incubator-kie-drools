package org.drools.core.beliefsystem.defeasible;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSetImpl.MODE;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSystem;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.LinkedList;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.runtime.beliefs.Mode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefeasibleBeliefSystem<M extends DefeasibleMode<M>> extends JTMSBeliefSystem<M>  {

    public DefeasibleBeliefSystem(NamedEntryPoint ep, TruthMaintenanceSystem tms) {
        super(ep, tms);
    }

    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new DefeasibleBeliefSet(this, fh);
    }

    public LogicalDependency<M> newLogicalDependency(Activation<M> activation, BeliefSet<M> beliefSet, Object object, Object value) {
        DefeasibleMode<M> mode;
        if ( value == null ) {
            mode = new DefeasibleMode(MODE.POSITIVE.getId(), this);
        } else if ( value instanceof String ) {
            if ( MODE.POSITIVE.getId().equals( value ) ) {
                mode = new DefeasibleMode(MODE.POSITIVE.getId(), this);
            }   else {
                mode = new DefeasibleMode(MODE.NEGATIVE.getId(), this);
            }
        } else {
            mode = new DefeasibleMode(((MODE)value).getId(), this);
        }

        DefeasibleLogicalDependency<M> dep = new DefeasibleLogicalDependency(activation, beliefSet, object, mode);
        mode.setLogicalDependency( dep );
        mode.initDefeats();
        return dep;
    }


    public void insert(LogicalDependency<M> node,
                       BeliefSet<M> beliefSet,
                       PropagationContext context,
                       ObjectTypeConf typeConf) {
        boolean wasEmpty = beliefSet.isEmpty();
        boolean wasNegated = beliefSet.isNegated();
        boolean wasUndecided = beliefSet.isUndecided();

        DefeasibleLogicalDependency dep = ( DefeasibleLogicalDependency ) node;

        super.insert( dep, beliefSet, context, typeConf );

        if ( ! wasEmpty && ! wasUndecided
             && ! beliefSet.isUndecided() && ! beliefSet.isEmpty() ) {

            DefeasibleBeliefSet<M> dbs = (DefeasibleBeliefSet<M>) beliefSet;

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
