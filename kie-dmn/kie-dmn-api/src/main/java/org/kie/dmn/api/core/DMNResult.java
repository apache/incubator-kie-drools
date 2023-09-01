package org.kie.dmn.api.core;

import java.util.List;

/**
 * An instance that encapsulates all the information
 * resulting from a DMN service invocation
 */
public interface DMNResult
        extends DMNMessageContainer {

    /**
     * Returns the full context, including
     * all input variables and additional
     * variables and decisions set during
     * the service invocation
     *
     * @return the resulting DMN context
     */
    DMNContext getContext();

    /**
     * Returns a list containing all the results
     * of the decisions executed
     *
     * @return A list with the result of the
     *         decisions
     */
    List<DMNDecisionResult> getDecisionResults();

    /**
     * Returns the result of a single decision.
     *
     * @param name the name of the decision
     *
     * @return the result of the decision
     *         or null if the decision was not
     *         evaluated.
     */
    DMNDecisionResult getDecisionResultByName( String name );

    /**
     * Returns the result of a single decision.
     *
     * @param id the id of the decision
     *
     * @return the result of the decision
     *         or null if the decision was not
     *         evaluated.
     */
    DMNDecisionResult getDecisionResultById( String id );

}
