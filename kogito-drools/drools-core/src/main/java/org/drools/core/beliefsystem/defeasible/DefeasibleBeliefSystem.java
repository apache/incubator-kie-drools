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

public class DefeasibleBeliefSystem extends JTMSBeliefSystem {

    public DefeasibleBeliefSystem(NamedEntryPoint ep, TruthMaintenanceSystem tms) {
        super(ep, tms);
    }

    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new DefeasibleBeliefSet(this, fh);
    }

    public LogicalDependency newLogicalDependency(Activation activation, BeliefSet beliefSet, Object object, Object value) {
        return new DefeasibleLogicalDependency(activation, beliefSet, object, value);
    }


}
