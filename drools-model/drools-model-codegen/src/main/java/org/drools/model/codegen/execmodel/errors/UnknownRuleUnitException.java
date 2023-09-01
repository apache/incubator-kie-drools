package org.drools.model.codegen.execmodel.errors;

public class UnknownRuleUnitException extends RuntimeException {

    private String ruleUnitName;

    public UnknownRuleUnitException(String ruleUnitName) {
        super();
        this.ruleUnitName = ruleUnitName;
    }

    @Override
    public String getMessage() {
        return "Unknown rule unit: " + ruleUnitName;
    }
}