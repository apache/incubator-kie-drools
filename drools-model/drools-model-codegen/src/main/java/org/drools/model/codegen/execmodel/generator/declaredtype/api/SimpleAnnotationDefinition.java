package org.drools.model.codegen.execmodel.generator.declaredtype.api;

import java.util.HashMap;
import java.util.Map;

public class SimpleAnnotationDefinition implements AnnotationDefinition {

    private final String name;
    private Map<String, String> values = new HashMap<>();

    public SimpleAnnotationDefinition(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getValueMap() {
        return values;
    }


    public AnnotationDefinition addValue(String key, String value) {
        values.put(key, value);
        return this;
    }

    @Override
    public boolean shouldAddAnnotation() {
        return true;
    }
}
