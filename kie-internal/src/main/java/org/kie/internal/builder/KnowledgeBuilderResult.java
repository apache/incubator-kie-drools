package org.kie.internal.builder;

import org.kie.api.io.Resource;

/**
 * A super interface for Knowledge Building result messages.
 */
public interface KnowledgeBuilderResult {

    /**
     * Returns the result severity
     * @return
     */
    ResultSeverity getSeverity();

    /**
     * Returns the result message
     */
    String getMessage();

    /**
     * Returns the lines that generated this result message in the source file
     * @return
     */
    int[] getLines();

    /**
     * Returns the Resource that caused this result
     * @return
     */
    Resource getResource();

    InternalMessage asMessage(long id);
}
