package org.drools.model.functions.accumulate;

import org.drools.model.Variable;

public class LegacyAccumulateFunction {
    private Variable var;
    protected final Class<?> functionClass;

    public LegacyAccumulateFunction(Class<?> functionClass) {
        this.functionClass = functionClass;
    }

    public Class<?> getFunctionClass() {
        return functionClass;
    }

    public Variable getVariable() {
        return var;
    }

    public LegacyAccumulateFunction as(Variable var) {
        this.var = var;
        return this;
    }
}
