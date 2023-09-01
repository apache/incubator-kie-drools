package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.dsl.AnnotationDescrBuilder;
import org.drools.drl.ast.dsl.EntryPointDeclarationDescrBuilder;
import org.drools.drl.ast.descr.EntryPointDeclarationDescr;

public class EntryPointDeclarationDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, EntryPointDeclarationDescr>
    implements
    EntryPointDeclarationDescrBuilder {

    protected EntryPointDeclarationDescrBuilderImpl(PackageDescrBuilder parent) {
        super( parent, new EntryPointDeclarationDescr() );
    }

    public EntryPointDeclarationDescrBuilder entryPointId( String id ) {
        descr.setEntryPointId( id );
        return this;
    }

    public AnnotationDescrBuilder<EntryPointDeclarationDescrBuilder> newAnnotation( String name ) {
        AnnotationDescrBuilder<EntryPointDeclarationDescrBuilder> annotation = new AnnotationDescrBuilderImpl<>( this, name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }

}
