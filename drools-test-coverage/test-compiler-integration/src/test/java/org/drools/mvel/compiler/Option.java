package org.drools.mvel.compiler;

public class Option<T> {

    private final T t;

    public Option(T t) {
        this.t = t;
    }

    public boolean isDefined() {
        return t != null;
    }

    public T get() {
        return t;
    }
}
