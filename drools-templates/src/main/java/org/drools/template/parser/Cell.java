package org.drools.template.parser;

import java.util.Map;

/**
 * A cell in a decision table
 */
public interface Cell<T> {
    Row getRow();

    Column getColumn();

    void setValue(String value);

    T getValue();

    void addValue(Map<String, Object> vars);

    void setIndex(int i);

    int getIndex();

    boolean isEmpty();
}
