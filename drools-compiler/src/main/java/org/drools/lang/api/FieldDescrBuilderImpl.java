package org.drools.lang.api;

import org.drools.lang.descr.AnnotationDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.TypeFieldDescr;

public class FieldDescrBuilderImpl extends BaseDescrBuilderImpl
    implements
    FieldDescrBuilder {

    protected FieldDescrBuilderImpl( String name ) {
        super( new TypeFieldDescr( name ) );
    }

    public TypeFieldDescr getDescr() {
        return (TypeFieldDescr) descr;
    }

    public AnnotationDescrBuilder newAnnotation( String name ) {
        AnnotationDescrBuilder annotation = new AnnotationDescrBuilderImpl( name );
        ((TypeFieldDescr) descr).addAnnotation( annotation.getDescr() );
        return annotation;
    }

    public FieldDescrBuilder index( int index ) {
        ((TypeFieldDescr) descr).setIndex( index );
        return this;
    }

    public FieldDescrBuilder name( String name ) {
        ((TypeFieldDescr) descr).setFieldName( name );
        return this;
    }

    public FieldDescrBuilder type( String type ) {
        ((TypeFieldDescr) descr).setPattern( new PatternDescr( type ) );
        return this;
    }

    public FieldDescrBuilder initialValue( String value ) {
        ((TypeFieldDescr) descr).setInitExpr( value );
        return this;
    }


}
