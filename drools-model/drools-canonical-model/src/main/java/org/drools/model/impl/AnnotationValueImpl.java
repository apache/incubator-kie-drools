package org.drools.model.impl;

import org.drools.model.AnnotationValue;

public class AnnotationValueImpl implements AnnotationValue {
    private final String key;
    private final Object value;

    public AnnotationValueImpl( String key, Object value ) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + " -> " + value;
    }
}
