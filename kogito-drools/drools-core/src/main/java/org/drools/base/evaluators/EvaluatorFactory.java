package org.drools.base.evaluators;

import java.io.Serializable;

import org.drools.spi.Evaluator;

public interface EvaluatorFactory
    extends
    Serializable {
    public Evaluator getEvaluator(Operator operator);
}
