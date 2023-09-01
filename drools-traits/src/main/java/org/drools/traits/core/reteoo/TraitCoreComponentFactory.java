package org.drools.traits.core.reteoo;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.reteoo.builder.NodeFactory;
import org.drools.traits.core.definitions.impl.TraitKnowledgePackageImpl;

public class TraitCoreComponentFactory implements CoreComponentFactory {

    private NodeFactory nodeFactory = TraitPhreakNodeFactory.getInstance();

    @Override
    public NodeFactory getNodeFactoryService() {
        return nodeFactory;
    }

    @Override
    public InternalKnowledgePackage createKnowledgePackage(String name) {
        return new TraitKnowledgePackageImpl(name);
    }

    @Override
    public int servicePriority() {
        return 1;
    }
}
