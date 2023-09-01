package org.drools.model.codegen.execmodel.generator.declaredtype.api;

import java.util.Map;

public interface AnnotationDefinition {

    String getName();

    Map<String, String> getValueMap();

    AnnotationDefinition addValue(String key, String value);

    boolean shouldAddAnnotation();
}
