package org.drools.model.codegen.execmodel.generator.declaredtype.api;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface FieldDefinition {

    String getFieldName();

    String getObjectType();

    String getInitExpr();

    default List<AnnotationDefinition> getFieldAnnotations() { return Collections.emptyList(); }

    default List<AnnotationDefinition> setterAnnotations() { return Collections.emptyList(); }

    default List<AnnotationDefinition> getterAnnotations() { return Collections.emptyList(); }

    boolean isKeyField();

    boolean createAccessors();

    boolean isStatic();

    boolean isFinal();

    default boolean isOverride() {
        return false;
    }

    default Optional<String> overriddenGetterName() {
        return Optional.empty();
    }

    default Optional<String> overriddenSetterName() {
        return Optional.empty();
    }

    default Optional<String> getJavadocComment() {
        return Optional.empty();
    }
}
