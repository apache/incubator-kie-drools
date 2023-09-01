package org.drools.beliefs.bayes.runtime;

import org.kie.api.KieBase;
import org.kie.api.internal.runtime.KieRuntimeService;

public class BayesRuntimeService implements KieRuntimeService<BayesRuntime> {
    @Override
    public BayesRuntime newKieRuntime(KieBase kieBase) {
        return new BayesRuntimeImpl( kieBase );
    }

    @Override
    public Class getServiceInterface() {
        return BayesRuntime.class;
    }
}
