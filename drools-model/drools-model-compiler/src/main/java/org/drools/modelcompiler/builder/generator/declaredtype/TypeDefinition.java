package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.Map;

interface TypeDefinition {

    String getTypeName();

    Map<String, TypeFieldDefinition> getFields();

    String getSuperTypeName();

    AnnotationDefinition[] getAnnotations();
}
