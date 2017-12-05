package org.drools.model.functions.accumulate;

import org.drools.model.Variable;

public class UserDefinedAccumulateFunction {
    private Variable var;
    protected final Variable source;
    protected final String functionName;

    public UserDefinedAccumulateFunction(Variable source, String functionName) {
        this.source = source;
        this.functionName = functionName;
    }

    public Variable getSource() {
        return source;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Variable getVariable() {
        return var;
    }

    public UserDefinedAccumulateFunction as(Variable var) {
        this.var = var;
        return this;
    }
}
