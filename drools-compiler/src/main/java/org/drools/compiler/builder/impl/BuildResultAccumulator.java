package org.drools.compiler.builder.impl;

import org.kie.internal.builder.KnowledgeBuilderResult;

public interface BuildResultAccumulator {
    void addBuilderResult(KnowledgeBuilderResult result);

    boolean hasErrors();
}
