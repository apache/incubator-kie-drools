package org.drools.compiler.builder.impl.processors;

import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;

public interface Processor {
    void process();
    Collection<? extends KnowledgeBuilderResult> getResults();
}
