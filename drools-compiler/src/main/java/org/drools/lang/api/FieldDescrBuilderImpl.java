package org.drools.lang.api;

import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.TypeFieldDescr;

public class FieldDescrBuilderImpl extends BaseDescrBuilderImpl<TypeFieldDescr>
    implements
    FieldDescrBuilder {

    protected FieldDescrBuilderImpl( String name ) {
        super( new TypeFieldDescr( name ) );
    }

    public AnnotationDescrBuilder newAnnotation( String name ) {
        AnnotationDescrBuilder annotation = new AnnotationDescrBuilderImpl( name );
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
        descr.setPattern( new PatternDescr( type ) );
        return this;
    }

    public FieldDescrBuilder initialValue( String value ) {
        descr.setInitExpr( value );
        return this;
    }


}
