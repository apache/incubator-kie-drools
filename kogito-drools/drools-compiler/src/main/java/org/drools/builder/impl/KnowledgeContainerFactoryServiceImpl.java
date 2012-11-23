package org.drools.builder.impl;

import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.KnowledgeContainer;
import org.kie.builder.KnowledgeContainerFactoryService;
import org.kie.builder.KnowledgeRepositoryScanner;

import java.lang.reflect.InvocationTargetException;

public class KnowledgeContainerFactoryServiceImpl implements KnowledgeContainerFactoryService {

    private static final String DEFAULT_SCANNER_IMPL = "org.drools.scanner.KnowledgeRepositoryScannerImpl";

    public KnowledgeContainer newKnowledgeContainer() {
        return newKnowledgeContainer(KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, getClass().getClassLoader()));
    }

    public KnowledgeContainer newKnowledgeContainer(KnowledgeBuilderConfiguration conf) {
        return new KnowledgeContainerImpl(conf);
    }

    public KnowledgeRepositoryScanner newKnowledgeScanner(KnowledgeContainer knowledgeContainer) {
        try {
            return getScannerClass().getConstructor(KnowledgeContainer.class).newInstance(knowledgeContainer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public KnowledgeRepositoryScanner newKnowledgeScanner(KnowledgeContainer knowledgeContainer, long pollingInterval) {
        try {
            return getScannerClass().getConstructor(KnowledgeContainer.class, long.class).newInstance(knowledgeContainer, pollingInterval);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Class<KnowledgeRepositoryScanner> getScannerClass() {
        try {
            return (Class<KnowledgeRepositoryScanner>)Class.forName(DEFAULT_SCANNER_IMPL);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find a KnowledgeRepositoryScanner implementation in the classpath", e);
        }
    }
}
