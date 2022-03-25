package org.drools.compiler.builder.impl;

import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;

/**
 * Builds the consequence in all of the {@link org.drools.compiler.compiler.PackageRegistry}
 * it contains, and provides the results.
 */
public interface PackageRegistryCompiler {

    void compileAll();

    void reloadAll();

    Collection<KnowledgeBuilderResult> getResults();
}
