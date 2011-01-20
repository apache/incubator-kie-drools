package org.drools.lang.api;

import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.TypeDeclarationDescr;

public class DeclareDescrBuilderImpl extends BaseDescrBuilderImpl
    implements
    DeclareDescrBuilder {

    protected DeclareDescrBuilderImpl() {
        super( new TypeDeclarationDescr() );
    }

    public TypeDeclarationDescr getDescr() {
        return (TypeDeclarationDescr) descr;
    }

    public AnnotationDescrBuilder newAnnotation( String name ) {
        AnnotationDescrBuilder annotation = new AnnotationDescrBuilderImpl( name );
        ((TypeDeclarationDescr) descr).addAnnotation( annotation.getDescr() );
        return annotation;
    }

    public DeclareDescrBuilder type( String type ) {
        ((TypeDeclarationDescr) descr).setTypeName( type );
        return this;
    }

}
