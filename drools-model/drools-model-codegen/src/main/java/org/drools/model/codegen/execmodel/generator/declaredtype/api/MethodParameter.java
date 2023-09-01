package org.drools.model.codegen.execmodel.generator.declaredtype.api;

public class MethodParameter {
    private final String type;
    private final String name;

    public MethodParameter(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
