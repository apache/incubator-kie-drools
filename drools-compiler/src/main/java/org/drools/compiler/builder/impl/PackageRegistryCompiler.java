package org.drools.compiler.builder.impl;

import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;

public interface PackageRegistryCompiler {

    void compileAll();

    void reloadAll();

    Collection<KnowledgeBuilderResult> getResults();
}
