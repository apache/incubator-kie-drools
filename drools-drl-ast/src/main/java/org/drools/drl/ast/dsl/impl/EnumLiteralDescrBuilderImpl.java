package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.AnnotationDescrBuilder;
import org.drools.drl.ast.dsl.EnumDeclarationDescrBuilder;
import org.drools.drl.ast.dsl.EnumLiteralDescrBuilder;
import org.drools.drl.ast.descr.EnumLiteralDescr;


public class EnumLiteralDescrBuilderImpl extends BaseDescrBuilderImpl<EnumDeclarationDescrBuilder, EnumLiteralDescr>
    implements
        EnumLiteralDescrBuilder {


    protected EnumLiteralDescrBuilderImpl( final EnumDeclarationDescrBuilder parent ) {
        super(parent, new EnumLiteralDescr() );
    }

    protected EnumLiteralDescrBuilderImpl( final EnumDeclarationDescrBuilder parent, final EnumLiteralDescr descr ) {
        super(parent, descr);
    }

    public EnumLiteralDescrBuilder index(int index) {
        descr.setIndex( index );
        return this;
    }

    public EnumLiteralDescrBuilder name(String name) {
        descr.setName( name );
        return this;
    }

    public EnumLiteralDescrBuilder constructorArg( String expr ) {
        descr.addConstructorArg( expr );
        return this;
    }

    public AnnotationDescrBuilder<EnumLiteralDescrBuilder> newAnnotation( String name ) {
        AnnotationDescrBuilder<EnumLiteralDescrBuilder> annotation = new AnnotationDescrBuilderImpl<>( this, name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }
}
