package org.drools.model.impl;

import org.drools.model.functions.Function0;

import static org.drools.model.impl.NamesGenerator.generateName;

public class Exchange<T> extends VariableImpl<T> {

    private T object;

    private Function0<T> messageSupplier;

    public Exchange(Class<T> type) {
        this(type, generateName("exchange"));
    }

    public Exchange(Class<T> type, String name) {
        super(type, name);
    }

    public T getObject() {
        return object;
    }

    public void setObject( T object ) {
        this.object = object;
    }

    public Function0<T> getMessageSupplier() {
        return messageSupplier;
    }

    public void setMessageSupplier( Function0<T> messageSupplier ) {
        this.messageSupplier = messageSupplier;
    }
}
