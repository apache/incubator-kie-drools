package org.drools.lang.api.impl;

import org.drools.lang.api.AnnotationDescrBuilder;
import org.drools.lang.api.DeclareDescrBuilder;
import org.drools.lang.api.FieldDescrBuilder;
import org.drools.lang.api.PackageDescrBuilder;
import org.drools.lang.descr.TypeDeclarationDescr;

public class DeclareDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, TypeDeclarationDescr>
    implements
    DeclareDescrBuilder {

    protected DeclareDescrBuilderImpl( PackageDescrBuilder parent ) {
        super( parent, new TypeDeclarationDescr() );
    }

    public DeclareDescrBuilder type( String type ) {
        descr.setTypeName( type );
        return this;
    }


    public DeclareDescrBuilder superType( String type ) {
        descr.setSuperTypeName( type );
        return this;
    }

    public AnnotationDescrBuilder<DeclareDescrBuilder> newAnnotation( String name ) {
        AnnotationDescrBuilder<DeclareDescrBuilder> annotation = new AnnotationDescrBuilderImpl<DeclareDescrBuilder>( this, name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }

    public FieldDescrBuilder newField( String name ) {
        FieldDescrBuilder field = new FieldDescrBuilderImpl( this, name );
        descr.addField( field.getDescr() );
        return field;
    }

}
