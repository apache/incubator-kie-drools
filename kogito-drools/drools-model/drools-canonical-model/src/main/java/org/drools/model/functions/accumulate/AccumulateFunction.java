package org.drools.model.functions.accumulate;

import org.drools.model.Variable;

public class AccumulateFunction {
    private Variable result;
    private Variable[] externalVars;

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

    public Variable getResult() {
        return result;
    }

    public AccumulateFunction as(Variable result) {
        this.result = result;
        return this;
    }

    public Variable[] getExternalVars() {
        return externalVars;
    }

    public AccumulateFunction with(Variable... externalVars) {
        this.externalVars = externalVars;
        return this;
    }
}
