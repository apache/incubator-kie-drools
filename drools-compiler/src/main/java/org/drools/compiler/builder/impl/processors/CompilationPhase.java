package org.drools.compiler.builder.impl.processors;

import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;

/**
 * Processes a PackageDescr and produces {@link KnowledgeBuilderResult}s.
 *
 * It usually analyzes a {@link org.drools.drl.ast.descr.PackageDescr}
 * and a {@link org.drools.compiler.compiler.PackageRegistry},
 * mutating them in place.
 *
 * This design originates from methods in {@link org.drools.compiler.builder.impl.KnowledgeBuilderImpl}
 * that have been moved to stand-alone classes, in order
 * to minimize the changes to the original code,
 * and it may change in the future.
 *
 */
public interface CompilationPhase {
    void process();

    Collection<? extends KnowledgeBuilderResult> getResults();
}
