package org.drools.modelcompiler.builder.generator.declaredtype;

import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.PatternDescr;

interface TypeFieldDefinition {

    String getFieldName();

    PatternDescr getPattern();

    String getInitExpr();

    AnnotationDefinition getAnnotation(String position);

    AnnotationDefinition[] getAnnotations();
}
