package org.drools.verifier.components;

import org.drools.verifier.report.components.Cause;

public class VariableRestriction extends Restriction
        implements
        Cause {

    protected Variable variable;

    public VariableRestriction(Pattern pattern) {
        super(pattern);
    }

    public Variable getVariable() {
        return variable;
    }

    public void setVariable(Variable patternVariable) {
        this.variable = patternVariable;
    }

    public RestrictionType getRestrictionType() {
        return Restriction.RestrictionType.VARIABLE;
    }

    @Override
    public String toString() {
        return "VariableRestriction from rule '" + getRuleName() + "' variable '" + variable + "'";
    }

}
