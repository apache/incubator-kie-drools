package org.drools.model.codegen.execmodel.generator.declaredtype.api;

import java.util.Collections;
import java.util.List;

public interface MethodDefinition {
    String getMethodName();

    String getReturnType();

    String getBody();

    default List<AnnotationDefinition> getAnnotations() {
        return Collections.emptyList();
    }

    boolean isStatic();

    boolean isPublic();

    default List<MethodParameter> parameters() {
        return Collections.emptyList();
    }
}