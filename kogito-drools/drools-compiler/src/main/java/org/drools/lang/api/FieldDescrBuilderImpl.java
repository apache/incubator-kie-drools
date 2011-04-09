package org.drools.lang.api;

import org.drools.compiler.TypeDeclarationError;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.lang.descr.TypeFieldDescr;
import org.drools.rule.TypeDeclaration;

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


    public FieldDescrBuilder processAnnotations() {
        if ( descr.getAnnotations().containsKey( TypeDeclaration.ATTR_FIELD_POSITION ) ) {
             int pos = Integer.valueOf(descr.getAnnotation( TypeDeclaration.ATTR_FIELD_POSITION ).getValue());
             descr.setIndex(pos);
        }
        return this;
    }


}
