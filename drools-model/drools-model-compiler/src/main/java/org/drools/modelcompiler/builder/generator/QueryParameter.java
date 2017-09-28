package org.drools.modelcompiler.builder.generator;

public class QueryParameter {
    final String name;
    final Class<?> type;

    public QueryParameter(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }
}
