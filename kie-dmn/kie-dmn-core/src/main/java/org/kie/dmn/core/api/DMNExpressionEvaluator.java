package org.kie.dmn.core.api;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;

/**
 * An Expression Evaluator interface for DMN defined expressions
 */
@FunctionalInterface
public interface DMNExpressionEvaluator {
    /**
     * Evaluates the expression, returning its result type (SUCCESS/FAILURE) and
     * result value.
     *
     * @param eventManager events manager to whom events are notified
     * @param result the result context instance
     *
     * @return the result of the evaluation of the expression
     */
    EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult result);

}
