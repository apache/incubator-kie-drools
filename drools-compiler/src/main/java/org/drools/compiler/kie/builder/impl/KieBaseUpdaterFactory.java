package org.drools.compiler.kie.builder.impl;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;

public interface KieBaseUpdaterFactory {

    KieBaseUpdater create(KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration, KieBaseUpdateContext ctx);
}
