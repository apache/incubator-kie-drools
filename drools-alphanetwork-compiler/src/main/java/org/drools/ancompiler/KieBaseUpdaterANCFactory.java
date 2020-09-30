package org.drools.ancompiler;

import org.drools.compiler.builder.InternalKnowledgeBuilder;
import org.drools.compiler.kie.builder.impl.KieBaseUpdateContext;
import org.drools.compiler.kie.builder.impl.KieBaseUpdater;
import org.drools.compiler.kie.builder.impl.KieBaseUpdaterFactory;

public class KieBaseUpdaterANCFactory implements KieBaseUpdaterFactory {

    @Override
    public KieBaseUpdater create(InternalKnowledgeBuilder knowledgeBuilder, KieBaseUpdateContext ctx) {
        return new KieBaseUpdaterANC(knowledgeBuilder, ctx);
    }
}
