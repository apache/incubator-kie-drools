package org.drools.ruleunits.impl;

public class UndefinedGeneratedRuleUnitVariableException extends IllegalArgumentException {

    private final String variable;
    private final String unit;

    public UndefinedGeneratedRuleUnitVariableException(String varName, String unitName) {
        super(String.format("Unknown variable '%s' for generated rule unit '%s'", varName, unitName));
        variable = varName;
        unit = unitName;
    }

    public String getVariable() {
        return variable;
    }

    public String getUnit() {
        return unit;
    }
}
