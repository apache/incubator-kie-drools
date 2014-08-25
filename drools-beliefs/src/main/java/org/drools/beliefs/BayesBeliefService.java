package org.drools.beliefs;

import org.drools.beliefs.bayes.BayesBeliefSystem;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.kie.internal.runtime.beliefs.KieBeliefService;

public class BayesBeliefService implements KieBeliefService {
    private int index;

    @Override
    public String getBeliefType() {
        return "Bayesian";
    }

    @Override
    public Class getServiceInterface() {
        return null;
    }

    public Object createBeliefSystem(Object ep,
                                     Object tms) {
        return new BayesBeliefSystem( (NamedEntryPoint)ep, (TruthMaintenanceSystem)tms);
    }

}