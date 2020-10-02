package org.drools.compiler.kie.builder.impl;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.reteoo.Rete;

public class KieBaseUpdatersContext {

    private final KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration;
    private final Rete rete;
    private final ClassLoader classLoader;

    public KieBaseUpdatersContext(KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration, Rete rete, ClassLoader classLoader) {
        this.knowledgeBuilderConfiguration = knowledgeBuilderConfiguration;
        this.rete = rete;
        this.classLoader = classLoader;
    }

    public KnowledgeBuilderConfigurationImpl getKnowledgeBuilderConfiguration() {
        return knowledgeBuilderConfiguration;
    }

    public Rete getRete() {
        return rete;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
