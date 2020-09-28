package org.drools.compiler.kie.builder.impl;

import java.util.Optional;

import org.drools.compiler.builder.InternalKnowledgeBuilder;

public interface KieBaseUpdaterFactory {

    Optional<Runnable> createWithKnowledgeBuilder(InternalKnowledgeBuilder internalKnowledgeBuilder);
}
