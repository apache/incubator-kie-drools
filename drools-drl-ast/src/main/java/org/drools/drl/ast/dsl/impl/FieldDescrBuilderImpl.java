package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.AnnotationDescrBuilder;
import org.drools.drl.ast.dsl.DescrBuilder;
import org.drools.drl.ast.dsl.FieldDescrBuilder;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.TypeFieldDescr;

public class FieldDescrBuilderImpl<T extends DescrBuilder<?,?>> extends BaseDescrBuilderImpl<T, TypeFieldDescr>
    implements
        FieldDescrBuilder<T> {

    protected FieldDescrBuilderImpl( T parent, String name ) {
        super( parent, new TypeFieldDescr( name ) );
    }

    public AnnotationDescrBuilder<FieldDescrBuilder<T>> newAnnotation( String name ) {
        AnnotationDescrBuilder<FieldDescrBuilder<T>> annotation = new AnnotationDescrBuilderImpl<>( this, name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }

    public FieldDescrBuilder index( int index ) {
        descr.setIndex( index );
        return this;
    }

    public FieldDescrBuilder name( String name ) {
        descr.setFieldName( name );
        return this;
    }

    public FieldDescrBuilder type( String type ) {
        descr.setPattern( new PatternDescr( type ) ); // resource set for new PatternDescr in setPattern
        return this;
    }

    public FieldDescrBuilder initialValue( String value ) {
        descr.setInitExpr( value );
        return this;
    }
}
