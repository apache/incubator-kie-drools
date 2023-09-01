package org.drools.model.codegen.execmodel.generator.declaredtype.api;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface TypeDefinition {

    String getTypeName();

    default List<? extends FieldDefinition> getFields() {
        return Collections.emptyList();
    }

    default List<FieldDefinition> getKeyFields() {
        return Collections.emptyList();
    }

    default Optional<String> getSuperTypeName() {
        return Optional.empty();
    }

    default List<String> getInterfacesNames() {
        return Collections.emptyList();
    }

    default List<AnnotationDefinition> getAnnotationsToBeAdded() {
        return Collections.emptyList();
    }

    default List<FieldDefinition> findInheritedDeclaredFields() {
        return Collections.emptyList();
    }

    default List<MethodDefinition> getMethods() {
        return Collections.emptyList();
    }

    default Optional<String> getJavadoc() {
        return Optional.empty();
    }
}
