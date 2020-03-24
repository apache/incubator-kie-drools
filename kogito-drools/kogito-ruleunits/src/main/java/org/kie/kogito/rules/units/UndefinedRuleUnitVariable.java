package org.kie.kogito.rules.units;

public class UndefinedRuleUnitVariable extends IllegalArgumentException {

    private final String variable;
    private final String unit;

    public UndefinedRuleUnitVariable(String varName, String unitName) {
        super(String.format("Unknown variable '%s' for rule unit '%s'", varName, unitName));
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
