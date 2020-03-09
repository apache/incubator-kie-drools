package org.drools.modelcompiler.builder.generator.declaredtype.api;

import java.util.Map;

public interface AnnotationDefinition {

    String getName();

    Map<String, String> getValueMap();

    String getNamespace();
}
