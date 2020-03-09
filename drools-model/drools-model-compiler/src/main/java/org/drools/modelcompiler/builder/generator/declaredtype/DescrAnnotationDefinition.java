package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.Collections;
import java.util.Map;

import org.drools.modelcompiler.builder.generator.declaredtype.api.AnnotationDefinition;

public class DescrAnnotationDefinition implements AnnotationDefinition {

    static final String VALUE = "value";

    private final String name;
    private final String namespace;
    private final Map<String, String> values;

    public DescrAnnotationDefinition(String name, String namespace, Map<String, String> values) {
        this.name = name;
        this.namespace = namespace;
        this.values = values;
    }

    public DescrAnnotationDefinition(String name, String namespace, String singleValue) {
        this(name, namespace, Collections.singletonMap(VALUE, singleValue));
    }

    public DescrAnnotationDefinition(String name, String namespace) {
        this(name, namespace, Collections.emptyMap());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getValueMap() {
        return values;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }
}
