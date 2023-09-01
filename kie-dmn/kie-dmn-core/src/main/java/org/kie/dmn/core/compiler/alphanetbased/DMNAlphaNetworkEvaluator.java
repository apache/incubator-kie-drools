package org.kie.dmn.core.compiler.alphanetbased;

import java.util.Optional;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.decisiontables.DecisionTable;
import org.kie.dmn.feel.runtime.events.InvalidInputEvent;

// TODO DT-ANC review these four names
public interface DMNAlphaNetworkEvaluator {

    Optional<InvalidInputEvent> validate(EvaluationContext evaluationContext);

    Object evaluate(EvaluationContext evaluationContext, DecisionTable decisionTable);

}
