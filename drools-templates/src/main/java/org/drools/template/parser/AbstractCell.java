package org.drools.template.parser;

import java.util.Map;

public abstract class AbstractCell<T> implements Cell<T> {

    protected final Row row;
    protected final Column column;
    protected int index;

    protected T value;

    protected AbstractCell(Row r, Column c) {
        row = r;
        column = c;
    }

    public String toString() {
        return "Cell[" + column + ": " + value + "]";
    }

    public Row getRow() {
        return row;
    }

    public Column getColumn() {
        return column;
    }

    public T getValue() {
        return value;
    }

    public void addValue(Map<String, Object> vars) {
        vars.put(column.getName(), value);
    }

    public void setIndex(int i) {
        index = i;
    }

    public int getIndex() {
        return index;
    }

    public boolean isEmpty() {
        return value == null;
    }
}
