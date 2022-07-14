package org.drools.ruleunits.dsl.accumulate;

import java.util.function.Supplier;

import org.drools.model.functions.Function1;

public class Accumulator1<A, B> {
    private final Function1<A, ?> bindingFunc;
    private final Supplier<?> accFuncSupplier;
    private final Class<B> accClass;

    public Accumulator1(Function1<A, ?> bindingFunc, Supplier<?> accFuncSupplier, Class<B> accClass) {
        this.bindingFunc = bindingFunc;
        this.accFuncSupplier = accFuncSupplier;
        this.accClass = accClass;
    }

    public Class<B> getAccClass() {
        return accClass;
    }

    public Function1<A, ?> getBindingFunc() {
        return bindingFunc;
    }

    public Supplier<?> getAccFuncSupplier() {
        return accFuncSupplier;
    }
}