package org.drools.modelcompiler.builder.generator.declaredtype.api;

import java.util.List;
import java.util.Optional;

public interface TypeDefinition {

    String getTypeName();

    List<TypeFieldDefinition> getFields();

    List<TypeFieldDefinition> getKeyFields();

    Optional<String> getSuperTypeName();

    List<AnnotationDefinition> getAnnotationsToBeAdded();

    List<TypeFieldDefinition> findInheritedDeclaredFields();

    String getJavaDocComment();
}
