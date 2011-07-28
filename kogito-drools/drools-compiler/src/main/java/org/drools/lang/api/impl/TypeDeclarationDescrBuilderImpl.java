package org.drools.lang.api.impl;

import org.drools.lang.api.AnnotationDescrBuilder;
import org.drools.lang.api.FieldDescrBuilder;
import org.drools.lang.api.PackageDescrBuilder;
import org.drools.lang.api.TypeDeclarationDescrBuilder;
import org.drools.lang.descr.TypeDeclarationDescr;

public class TypeDeclarationDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, TypeDeclarationDescr>
    implements
    TypeDeclarationDescrBuilder {

    protected TypeDeclarationDescrBuilderImpl( PackageDescrBuilder parent ) {
        super( parent, new TypeDeclarationDescr() );
    }

    public TypeDeclarationDescrBuilder name( String type ) {
        descr.setTypeName( type );
        return this;
    }


    public TypeDeclarationDescrBuilder superType( String type ) {
        descr.setSuperTypeName( type );
        return this;
    }

    public AnnotationDescrBuilder<TypeDeclarationDescrBuilder> newAnnotation( String name ) {
        AnnotationDescrBuilder<TypeDeclarationDescrBuilder> annotation = new AnnotationDescrBuilderImpl<TypeDeclarationDescrBuilder>( this, name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }

    public FieldDescrBuilder newField( String name ) {
        FieldDescrBuilder field = new FieldDescrBuilderImpl( this, name );
        descr.addField( field.getDescr() );
        return field;
    }

}
