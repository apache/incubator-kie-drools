package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.AnnotationDescr;

/**
 *  A descriptor builder for annotations
 */
public interface AnnotationDescrBuilder<P extends DescrBuilder< ? , ? >>
    extends
    DescrBuilder<P, AnnotationDescr>,
    AnnotatedDescrBuilder<AnnotationDescrBuilder<P>> {

    public AnnotationDescrBuilder<P> value( Object value );

    public AnnotationDescrBuilder<P> keyValue( String key,
                                               Object value );

}
