package org.drools.model.codegen.execmodel.generator;

public class QueryParameter {
    private final String name;
    private final Class<?> type;
    private final int index;

    public QueryParameter(String name, Class<?> type, int index) {
        this.name = name;
        this.type = type;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }
}
