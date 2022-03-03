package org.drools.compiler.builder.impl.processors;

import org.kie.internal.builder.ResourceChange;

/**
 * A method reference to
 * {@link org.drools.compiler.builder.impl.KnowledgeBuilderImpl#filterAccepts(ResourceChange.Type, String, String)}
 * or to {@link org.drools.compiler.builder.impl.KnowledgeBuilderImpl#filterAcceptsRemoval(ResourceChange.Type, String, String)}
 *
 * It is necessary to ease the refactoring of some methods in {@link org.drools.compiler.builder.impl.KnowledgeBuilderImpl}
 * into {@link CompilationPhase}s.
 *
 */
public interface FilterCondition {
    boolean accepts(ResourceChange.Type type, String namespace, String name);
}
