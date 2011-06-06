package org.drools.lang.api.impl;

import org.drools.lang.api.AnnotationDescrBuilder;
import org.drools.lang.api.DeclareDescrBuilder;
import org.drools.lang.api.FieldDescrBuilder;
import org.drools.lang.descr.TypeDeclarationDescr;

public class DeclareDescrBuilderImpl extends BaseDescrBuilderImpl<TypeDeclarationDescr>
    implements
    DeclareDescrBuilder {

    protected DeclareDescrBuilderImpl() {
        super( new TypeDeclarationDescr() );
    }

    public DeclareDescrBuilder type( String type ) {
        descr.setTypeName( type );
        return this;
    }


    public DeclareDescrBuilder superType( String type ) {
        descr.setSuperTypeName( type );
        return this;
    }

    public AnnotationDescrBuilder newAnnotation( String name ) {
        AnnotationDescrBuilder annotation = new AnnotationDescrBuilderImpl( name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }

    public FieldDescrBuilder newField( String name ) {
        FieldDescrBuilder field = new FieldDescrBuilderImpl( name );
        descr.addField( field.getDescr() );
        return field;
    }

}
