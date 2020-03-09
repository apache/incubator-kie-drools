package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.List;

interface TypeFieldDefinition {

    String getFieldName();

    String getObjectType();

    String getInitExpr();

    List<AnnotationDefinition> getAnnotations();

    void addAnnotation(String name);

    void addAnnotation(String name, String value);

    boolean isKeyField();

    boolean createAccessors();

    boolean isStatic();

    boolean isFinal();
}
