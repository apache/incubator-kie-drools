package org.drools.model.functions.accumulate;

import org.drools.model.Variable;

public class AccumulateFunction {
    private Variable var;
    protected final Variable source;
    protected final Class<?> functionClass;

    public AccumulateFunction(Variable source, Class<?> functionClass) {
        this.source = source;
        this.functionClass = functionClass;
    }

    public Variable getSource() {
        return source;
    }

    public Class<?> getFunctionClass() {
        return functionClass;
    }

    public Variable getVariable() {
        return var;
    }

    public AccumulateFunction as(Variable var) {
        this.var = var;
        return this;
    }
}
