package org.drools.lang.api;

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
