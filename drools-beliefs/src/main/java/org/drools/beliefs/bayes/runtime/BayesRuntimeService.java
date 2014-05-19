package org.drools.beliefs.bayes.runtime;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.kie.internal.runtime.KieRuntimeService;
import org.kie.internal.runtime.KnowledgeRuntime;

public class BayesRuntimeService implements KieRuntimeService<BayesRuntime> {
    @Override
    public BayesRuntime newKieRuntime(KnowledgeRuntime session) {
        return new BayesRuntimeImpl( (InternalKnowledgeRuntime) session );
    }

    @Override
    public Class getServiceInterface() {
        return BayesRuntime.class;
    }
}
