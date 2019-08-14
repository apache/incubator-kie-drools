package org.drools.model.functions.accumulate;

import java.util.function.Supplier;

import org.drools.model.Argument;
import org.drools.model.Value;
import org.drools.model.Variable;

public class AccumulateFunction {
    private Variable result;
    private Variable[] externalVars;

    protected final Argument source;
    protected final Supplier<?> functionSupplier;

    public AccumulateFunction(Argument source, Supplier<?> functionSupplier) {
        this.source = source;
        this.functionSupplier = functionSupplier;
    }

    public Argument getSource() {
        return source;
    }

    public Object createFunctionObject() {
        return functionSupplier.get();
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
