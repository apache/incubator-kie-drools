package org.drools.drl.ast.dsl;

/**
 * An interface for DescrBuilders that support annotations
 */
public interface AnnotatedDescrBuilder<P extends DescrBuilder<?,?>> {

    public AnnotationDescrBuilder<P> newAnnotation( String name );

}
