package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.Map;

interface AnnotationDefinition {

    Object getValue(final String key);

    String getName();

    Map<String, Object> getValueMap();

    String getNamespace();

    String getValuesAsString();

    String getValue();
}
