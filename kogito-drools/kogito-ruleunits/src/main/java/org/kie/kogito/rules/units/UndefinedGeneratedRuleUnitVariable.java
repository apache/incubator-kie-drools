package org.kie.kogito.rules.units;

public class UndefinedGeneratedRuleUnitVariable extends IllegalArgumentException {

    private final String variable;
    private final String unit;

    public UndefinedGeneratedRuleUnitVariable(String varName, String unitName) {
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
