package org.drools.modelcompiler.builder.generator.declaredtype;

interface TypeFieldDefinition {

    String getFieldName();

    String getObjectType();

    String getInitExpr();

    AnnotationDefinition getAnnotation(String position);

    AnnotationDefinition[] getAnnotations();
}
