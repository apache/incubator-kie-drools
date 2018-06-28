package org.drools.model.functions.accumulate;

import org.drools.model.Argument;
import org.drools.model.Value;
import org.drools.model.Variable;

public class AccumulateFunction {
    private Variable result;
    private Variable[] externalVars;

    protected final Argument source;
    protected final Class<?> functionClass;

    public AccumulateFunction(Argument source, Class<?> functionClass) {
        this.source = source;
        this.functionClass = functionClass;
    }

    public Argument getSource() {
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

    public boolean isFixedValue() {
        return source instanceof Value;
    }
}
