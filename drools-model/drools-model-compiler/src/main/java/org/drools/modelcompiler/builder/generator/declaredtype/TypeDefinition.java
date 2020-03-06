package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.Collection;

interface TypeDefinition {

    String getTypeName();

    Collection<TypeFieldDefinition> getFields();

    String getSuperTypeName();

    AnnotationDefinition[] getAnnotations();
}
