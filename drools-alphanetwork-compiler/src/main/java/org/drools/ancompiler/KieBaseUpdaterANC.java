package org.drools.ancompiler;

import java.util.Optional;

import org.drools.compiler.builder.InternalKnowledgeBuilder;
import org.drools.compiler.kie.builder.impl.KieBaseUpdaterFactory;

public class KieBaseUpdaterANC implements KieBaseUpdaterFactory {

    InternalKnowledgeBuilder knowledgeBuilder;

    @Override
    public Optional<Runnable> createWithKnowledgeBuilder(InternalKnowledgeBuilder internalKnowledgeBuilder) {
        this.knowledgeBuilder = internalKnowledgeBuilder;
        return Optional.of(this::run);
    }

    public void run() {

        // find the new compiled alpha network in the classpath, if it's not there,
        // generate compile it and reattach it

        System.out.println("should update here");

    }
}
