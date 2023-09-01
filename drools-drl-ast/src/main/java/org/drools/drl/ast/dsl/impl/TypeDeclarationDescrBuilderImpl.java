package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.FieldDescrBuilder;
import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.dsl.AbstractClassTypeDeclarationBuilder;
import org.drools.drl.ast.dsl.AnnotationDescrBuilder;
import org.drools.drl.ast.dsl.TypeDeclarationDescrBuilder;
import org.drools.drl.ast.descr.TypeDeclarationDescr;

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
        descr.addSuperType( type );
        return this;
    }

    public TypeDeclarationDescrBuilder setTrait( boolean trait ) {
        descr.setTrait( trait );
        return this;
    }

    public AnnotationDescrBuilder<TypeDeclarationDescrBuilder> newAnnotation( String name ) {
        AnnotationDescrBuilder<TypeDeclarationDescrBuilder> annotation = new AnnotationDescrBuilderImpl<>( this, name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }

    public FieldDescrBuilder<AbstractClassTypeDeclarationBuilder<TypeDeclarationDescr>> newField( String name ) {
        FieldDescrBuilder<AbstractClassTypeDeclarationBuilder<TypeDeclarationDescr>> field = new FieldDescrBuilderImpl( this, name );
        descr.addField( field.getDescr() );
        return field;
    }

}
