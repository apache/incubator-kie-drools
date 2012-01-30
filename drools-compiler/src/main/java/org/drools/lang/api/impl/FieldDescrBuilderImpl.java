package org.drools.lang.api.impl;

import org.drools.lang.api.AnnotationDescrBuilder;
import org.drools.lang.api.DescrBuilder;
import org.drools.lang.api.FieldDescrBuilder;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.TypeFieldDescr;
import org.drools.rule.TypeDeclaration;

public class FieldDescrBuilderImpl<T extends DescrBuilder<?,?>> extends BaseDescrBuilderImpl<T, TypeFieldDescr>
    implements
    FieldDescrBuilder<T> {

    protected FieldDescrBuilderImpl( T parent, String name ) {
        super( parent, new TypeFieldDescr( name ) );
    }

    public AnnotationDescrBuilder<FieldDescrBuilder<T>> newAnnotation( String name ) {
        AnnotationDescrBuilder<FieldDescrBuilder<T>> annotation = new AnnotationDescrBuilderImpl<FieldDescrBuilder<T>>( this, name );
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


    public FieldDescrBuilder processAnnotations() {
        if ( descr.getAnnotations().containsKey( TypeDeclaration.ATTR_FIELD_POSITION ) ) {
             int pos = Integer.valueOf(descr.getAnnotation( TypeDeclaration.ATTR_FIELD_POSITION ).getSingleValue());
             descr.setIndex(pos);
        }
        return this;
    }


}
