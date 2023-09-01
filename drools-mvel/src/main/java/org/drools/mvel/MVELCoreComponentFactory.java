package org.drools.mvel;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.core.reteoo.CoreComponentFactory;

public class MVELCoreComponentFactory extends CoreComponentFactory.DroolsCoreComponentFactory {

    @Override
    public InternalKnowledgePackage createKnowledgePackage(String name) {
        return new MVELKnowledgePackageImpl(name);
    }
}
