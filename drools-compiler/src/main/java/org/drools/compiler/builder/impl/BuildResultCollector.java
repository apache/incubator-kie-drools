package org.drools.compiler.builder.impl;

import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.KnowledgeBuilderResults;
import org.kie.internal.builder.ResultSeverity;

import java.util.Collection;

/**
 * Holds build processing info, warnings and errors.
 */
public interface BuildResultCollector {
    void addBuilderResult(KnowledgeBuilderResult result);

    /**
     * This will return true if there were errors in the package building and
     * compiling phase
     */
    boolean hasErrors();

    /**
     * Return the knowledge builder results for the listed severities.
     *
     * @param severities
     */
    KnowledgeBuilderResults getResults(ResultSeverity... severities);

    boolean hasResults(ResultSeverity... problemTypes);

    default Collection<? extends KnowledgeBuilderResult> getAllResults() {
        return getResults(ResultSeverity.values());
    }

    default void add(KnowledgeBuilderResult result) {
        addBuilderResult(result);
    }

    default void addAll(Collection<? extends KnowledgeBuilderResult> results) {
        for (KnowledgeBuilderResult result : results) {
            add(result);
        }
    }
}
