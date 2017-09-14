package org.drools.model;

import org.drools.model.functions.FunctionN;

public interface InvokerSingleValuePattern<T> extends InvokerPattern<T> {

    FunctionN<T> getInvokedFunction();
}
