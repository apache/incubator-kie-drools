package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.Collections;
import java.util.Map;

public class DescrDeclaredTypeAnnotationDefinition implements AnnotationDefinition {

    private static final String VALUE = "value";

    private final String name;
    private final String namespace;
    private final Map<String, Object> values;

    public DescrDeclaredTypeAnnotationDefinition(String name, String namespace, Map<String, Object> values) {
        this.name = name;
        this.namespace = namespace;
        this.values = values;
    }

    public DescrDeclaredTypeAnnotationDefinition(String name, String namespace, Object singleValue) {
        this(name, namespace, Collections.singletonMap(VALUE, singleValue));
    }

    public DescrDeclaredTypeAnnotationDefinition(String name, String namespace) {
        this(name, namespace, Collections.emptyMap());
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
        return values;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public String getValuesAsString() {
        return null;
    }
}
