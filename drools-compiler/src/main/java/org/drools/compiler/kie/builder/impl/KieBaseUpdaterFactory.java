package org.drools.compiler.kie.builder.impl;

import org.drools.compiler.builder.InternalKnowledgeBuilder;

public interface KieBaseUpdaterFactory {

    KieBaseUpdater create(InternalKnowledgeBuilder internalKnowledgeBuilder, KieBaseUpdateContext ctx);
}
