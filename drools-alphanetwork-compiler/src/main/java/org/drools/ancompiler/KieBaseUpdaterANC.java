package org.drools.ancompiler;

import java.util.Optional;

import org.drools.compiler.builder.InternalKnowledgeBuilder;
import org.drools.compiler.kie.builder.impl.KieBaseUpdaterFactory;

public class KieBaseUpdaterANC implements KieBaseUpdaterFactory {

    @Override
    public Optional<Runnable> createWithKnowledgeBuilder(InternalKnowledgeBuilder internalKnowledgeBuilder) {
        return Optional.of(new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello world");
            }
        });
    }
}
