package org.kie.dmn.api.core;

import java.util.List;

/**
 * Stores the result of the evaluation of a decision
 *
 */
public interface DMNDecisionResult {

    enum DecisionEvaluationStatus {
        NOT_EVALUATED, EVALUATING, SUCCEEDED, SKIPPED, FAILED;
    }

    /**
     * Returns the decision ID
     *
     * @return the decision ID
     */
    String getDecisionId();

    /**
     * Returns the decision name
     *
     * @return the decision name
     */
    String getDecisionName();

    /**
     * Returns the evaluation status
     * of this decision.
     *
     * @return SUCCEEDED if the evaluation completed
     *         without errors.
     */
    DecisionEvaluationStatus getEvaluationStatus();

    /**
     * Returns the result of the evaluation
     * of the decision
     *
     * @return the result of the decision
     */
    Object getResult();

    /**
     * Returns a list of DMN messages generated
     * during the evaluation of this decision.
     *
     * @return a list of messages, or an empty list if
     *         no message was generated
     */
    List<DMNMessage> getMessages();

    /**
     * Returns true if any error occurred during evaluation.
     *
     * @return true if any error ocurred during evaluation.
     */
    boolean hasErrors();

}
