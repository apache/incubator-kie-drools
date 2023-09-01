package org.kie.api.runtime.rule;

import java.io.Serializable;

/**
 * A public interface to be implemented by all evaluators
 */
public interface Evaluator
    extends
    Serializable {

    /**
     * @return the operator representation object for this evaluator
     */
    Operator getOperator();

    /**
     * Returns true if this evaluator implements a temporal evaluation,
     * i.e., a time sensitive evaluation whose properties of matching
     * only events within an specific time interval can be used for
     * determining event expirations automatically.
     *
     * @return true if the evaluator is a temporal evaluator.
     */
    boolean isTemporal();

}
