package org.kie.pmml.evaluator.assembler;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.ResourceTypePackageRegistry;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.internal.weaver.KieWeaverService;
import org.kie.api.io.ResourceType;
import org.kie.pmml.evaluator.api.container.PMMLPackage;
import org.kie.pmml.evaluator.assembler.container.PMMLPackageImpl;

public class PMMLWeaverService implements KieWeaverService<PMMLPackage> {

    @Override
    public ResourceType getResourceType() {
        return ResourceType.PMML;
    }

    @Override
    public void merge(KieBase kieBase, KiePackage kiePkg, PMMLPackage dmnpkg) {
        ResourceTypePackageRegistry registry = ((InternalKnowledgePackage) kiePkg).getResourceTypePackages();
        PMMLPackageImpl existing = registry.computeIfAbsent(ResourceType.PMML, rt -> new PMMLPackageImpl());
        existing.addAll(dmnpkg.getAllModels().values());
    }

    @Override
    public void weave(KieBase kieBase, KiePackage kiePkg, PMMLPackage rtPkg) {
        // nothing to do for now
    }
}