package org.drools.beliefs.bayes.weaver;

import org.drools.beliefs.bayes.assembler.BayesPackage;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.kie.api.definition.KiePackage;
import org.kie.api.internal.weaver.KieWeaverService;
import org.kie.api.io.ResourceType;

public class BayesWeaverService implements KieWeaverService<BayesPackage> {

    @Override
    public ResourceType getResourceType() {
        return ResourceType.BAYES;
    }

    @Override
    public void merge(KiePackage kiePkg, BayesPackage bayesPkg) {
        InternalKnowledgePackage internalPkg = (InternalKnowledgePackage) kiePkg;
        BayesPackage registeredPkg = internalPkg.getResourceTypePackages()
                .computeIfAbsent(ResourceType.BAYES, rt -> new BayesPackage(kiePkg.getName()));

        for (String name : bayesPkg.listJunctionTrees()) {
            registeredPkg.addJunctionTree(name, bayesPkg.getJunctionTree(name));
        }
    }

    @Override
    public void weave(KiePackage kiePkg, BayesPackage rtPkg) {
        System.out.println("Hello World ");
    }
}
