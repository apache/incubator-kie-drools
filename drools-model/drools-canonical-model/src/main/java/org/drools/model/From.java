package org.drools.model;

public interface From<A> extends DeclarationSource {
    Variable<A> getVariable();
    boolean isReactive();
}
