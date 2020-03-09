package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.Map;

interface AnnotationDefinition {

    Object getValue(final String key);

    String getName();

    Map<String, String> getValueMap();

    String getNamespace();

    String getValuesAsString();
}
