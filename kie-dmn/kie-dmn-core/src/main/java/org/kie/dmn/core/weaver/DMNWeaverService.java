package org.kie.dmn.core.weaver;

import java.util.Map;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.ResourceTypePackageRegistry;
import org.kie.api.definition.KiePackage;
import org.kie.api.internal.weaver.KieWeaverService;
import org.kie.api.io.ResourceType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNPackage;
import org.kie.dmn.core.impl.DMNPackageImpl;

public class DMNWeaverService implements KieWeaverService<DMNPackage> {

    @Override
    public ResourceType getResourceType() {
        return ResourceType.DMN;
    }

    @Override
    public void merge(KiePackage kiePkg, DMNPackage dmnpkg) {
        ResourceTypePackageRegistry registry = ((InternalKnowledgePackage)kiePkg).getResourceTypePackages();
        DMNPackageImpl existing = registry.computeIfAbsent(ResourceType.DMN, rt -> new DMNPackageImpl(dmnpkg.getNamespace()));

        for ( Map.Entry<String, DMNModel> entry : dmnpkg.getAllModels().entrySet() ) {
            existing.addModel( entry.getKey(), entry.getValue() );
        }

        existing.addProfiles(((DMNPackageImpl) dmnpkg).getProfiles());
    }

    @Override
    public void weave(KiePackage kiePkg, DMNPackage rtPkg) {
        // nothing to do for now
    }
}
