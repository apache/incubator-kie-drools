package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.Map;

public class DescrDeclaredTypeAnnotationDefinition implements AnnotationDefinition {

    private final String name;
    private final String namespace;
    private final String value;

    public DescrDeclaredTypeAnnotationDefinition(String name, String namespace, String value) {
        this.name = name;
        this.namespace = namespace;
        this.value = value;
    }

    @Override
    public Object getValue(String key) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, Object> getValueMap() {
        return null;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public String getValuesAsString() {
        return null;
    }

    @Override
    public String getValue() {
        return value;
    }
}
