package org.drools.modelcompiler.constraints;

import org.drools.model.functions.PredicateInformation;

import static org.drools.core.util.MessageUtils.formatConstraintErrorMessage;

public class ConstraintEvaluationException extends RuntimeException {

    private static final long serialVersionUID = 7880877148568087603L;

    public ConstraintEvaluationException(PredicateInformation predicateInformation, Throwable cause) {
        super(formatConstraintErrorMessage(predicateInformation.getStringConstraint(), predicateInformation.getRuleNameMap(), predicateInformation.isMoreThanMaxRuleDefs()), cause);
    }
}
