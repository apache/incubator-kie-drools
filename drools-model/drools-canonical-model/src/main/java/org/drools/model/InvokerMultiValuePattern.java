package org.drools.model;

import org.drools.model.functions.FunctionN;

public interface InvokerMultiValuePattern<T> extends InvokerPattern<T> {

    FunctionN<Iterable<? extends T>> getInvokedFunction();
}
