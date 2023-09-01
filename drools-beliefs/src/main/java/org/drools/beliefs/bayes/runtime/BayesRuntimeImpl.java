package org.drools.beliefs.bayes.runtime;

import org.drools.beliefs.bayes.BayesInstance;
import org.drools.beliefs.bayes.JunctionTree;
import org.drools.beliefs.bayes.assembler.BayesPackage;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.ResourceTypePackageRegistry;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;

public class BayesRuntimeImpl implements BayesRuntime {

    private KieBase kieBase;

    public BayesRuntimeImpl(KieBase kieBase) {
        this.kieBase = kieBase;
    }

    public BayesInstance createInstance(Class cls) {
        InternalKnowledgePackage kpkg = (InternalKnowledgePackage) kieBase.getKiePackage(cls.getPackage().getName());
        ResourceTypePackageRegistry map = kpkg.getResourceTypePackages();
        BayesPackage bayesPkg = (BayesPackage) map.get(ResourceType.BAYES);
        JunctionTree jtree = bayesPkg.getJunctionTree(cls.getSimpleName());

        return new BayesInstance(jtree, cls);
    }
}
