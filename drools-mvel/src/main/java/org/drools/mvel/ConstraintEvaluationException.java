package org.drools.mvel;

import org.drools.mvel.MVELConstraint.EvaluationContext;

import static org.drools.core.util.MessageUtils.formatConstraintErrorMessage;

public class ConstraintEvaluationException extends RuntimeException {

    private static final long serialVersionUID = -3413225194510143529L;

    public ConstraintEvaluationException(String expression, EvaluationContext evaluationContext, Throwable cause) {
        super(formatConstraintErrorMessage(expression, evaluationContext.getRuleNameMap(), evaluationContext.isMoreThanMaxRuleDefs()), cause);
    }
}
