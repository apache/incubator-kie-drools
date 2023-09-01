package org.drools.core.reteoo;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.impl.KnowledgePackageImpl;
import org.drools.core.reteoo.builder.NodeFactory;
import org.drools.core.reteoo.builder.PhreakNodeFactory;
import org.kie.api.internal.utils.KieService;

public interface CoreComponentFactory extends KieService {

    NodeFactory getNodeFactoryService();

    InternalKnowledgePackage createKnowledgePackage(String name);

    class Holder {
        private static final CoreComponentFactory INSTANCE = KieService.load( CoreComponentFactory.class );
    }

    static CoreComponentFactory get() {
        return CoreComponentFactory.Holder.INSTANCE != null ? CoreComponentFactory.Holder.INSTANCE : DroolsCoreComponentFactory.INSTANCE;
    }

    class DroolsCoreComponentFactory implements CoreComponentFactory {

        private static final DroolsCoreComponentFactory INSTANCE = new DroolsCoreComponentFactory();

        private NodeFactory nodeFactory = PhreakNodeFactory.getInstance();

        public NodeFactory getNodeFactoryService() {
            return nodeFactory;
        }

        public InternalKnowledgePackage createKnowledgePackage(String name) {
            return new KnowledgePackageImpl(name);
        }
    }
}
