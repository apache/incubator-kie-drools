package org.drools.modelcompiler.builder.generator.declaredtype.api;

import java.util.List;

public interface TypeFieldDefinition {

    String getFieldName();

    String getObjectType();

    String getInitExpr();

    List<AnnotationDefinition> getAnnotations();

    boolean isKeyField();

    boolean createAccessors();

    boolean isStatic();

    boolean isFinal();
}
