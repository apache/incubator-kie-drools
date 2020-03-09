package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.List;

interface TypeDefinition {

    String getTypeName();

    List<TypeFieldDefinition> getFields();

    List<TypeFieldDefinition> getKeyFields();

    String getSuperTypeName();

    List<AnnotationDefinition> getAnnotations();

    List<AnnotationDefinition> getSoftAnnotations();

    List<TypeFieldDefinition> findInheritedDeclaredFields();

    String getJavaDocComment();
}
