package org.drools.model;

public interface Declaration<T> extends Variable<T> {

    DeclarationSource getSource();

    Window getWindow();

    DomainClassMetadata getMetadata();
}
