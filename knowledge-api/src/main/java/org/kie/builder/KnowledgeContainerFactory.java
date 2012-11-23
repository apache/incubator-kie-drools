package org.kie.builder;

import org.kie.util.ServiceRegistryImpl;

public class KnowledgeContainerFactory {
    private static volatile KnowledgeContainerFactoryService factoryService;

    public static KnowledgeContainer newKnowledgeContainer() {
        return getKnowledgeContainerServiceFactory().newKnowledgeContainer();
    }

    public static KnowledgeContainer newKnowledgeContainer(KnowledgeBuilderConfiguration conf) {
        return getKnowledgeContainerServiceFactory().newKnowledgeContainer(conf);
    }

    public static KnowledgeRepositoryScanner newKnowledgeScanner(KnowledgeContainer knowledgeContainer) {
        return getKnowledgeContainerServiceFactory().newKnowledgeScanner(knowledgeContainer);
    }

    public static KnowledgeRepositoryScanner newKnowledgeScanner(KnowledgeContainer knowledgeContainer, long pollingInterval) {
        return getKnowledgeContainerServiceFactory().newKnowledgeScanner(knowledgeContainer, pollingInterval);
    }

    private static synchronized void setKnowledgeBuilderFactoryService(KnowledgeContainerFactoryService serviceFactory) {
        factoryService = serviceFactory;
    }

    private static synchronized KnowledgeContainerFactoryService getKnowledgeContainerServiceFactory() {
        if ( factoryService == null ) {
            loadServiceFactory();
        }
        return factoryService;
    }

    private static void loadServiceFactory() {
        setKnowledgeBuilderFactoryService( ServiceRegistryImpl.getInstance().get( KnowledgeContainerFactoryService.class ) );
    }
}
