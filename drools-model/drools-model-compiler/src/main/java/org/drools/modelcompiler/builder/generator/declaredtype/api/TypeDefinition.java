package org.drools.modelcompiler.builder.generator.declaredtype.api;

import java.util.List;

public interface TypeDefinition {

    String getTypeName();

    List<TypeFieldDefinition> getFields();

    List<TypeFieldDefinition> getKeyFields();

    String getSuperTypeName();

    List<AnnotationDefinition> getAnnotations();

    List<TypeFieldDefinition> findInheritedDeclaredFields();

    String getJavaDocComment();
}
