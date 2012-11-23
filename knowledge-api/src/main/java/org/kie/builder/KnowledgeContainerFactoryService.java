package org.kie.builder;

import org.kie.Service;

public interface KnowledgeContainerFactoryService extends Service {
    KnowledgeContainer newKnowledgeContainer();

    KnowledgeContainer newKnowledgeContainer(KnowledgeBuilderConfiguration conf);

    KnowledgeRepositoryScanner newKnowledgeScanner(KnowledgeContainer knowledgeContainer);

    KnowledgeRepositoryScanner newKnowledgeScanner(KnowledgeContainer knowledgeContainer, long pollingInterval);
}
