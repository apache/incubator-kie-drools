package org.drools.compiler.kie.builder.impl;

import java.util.Optional;

import org.drools.compiler.builder.InternalKnowledgeBuilder;

public interface KieBaseUpdater extends Runnable {

    Optional<InternalKnowledgeBuilder> precreatedKnowledgeBuilder();
}
