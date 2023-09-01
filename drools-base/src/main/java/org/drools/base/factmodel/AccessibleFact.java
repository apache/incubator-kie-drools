package org.drools.base.factmodel;

public interface AccessibleFact {
    Object getValue(String fieldName);
    void setValue(String fieldName, Object value);
}
