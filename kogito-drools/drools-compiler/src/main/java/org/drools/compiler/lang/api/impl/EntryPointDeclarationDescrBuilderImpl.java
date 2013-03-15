package org.drools.compiler.lang.api.impl;

import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.AnnotationDescrBuilder;
import org.drools.compiler.lang.api.EntryPointDeclarationDescrBuilder;
import org.drools.compiler.lang.descr.EntryPointDeclarationDescr;

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
        AnnotationDescrBuilder<EntryPointDeclarationDescrBuilder> annotation = new AnnotationDescrBuilderImpl<EntryPointDeclarationDescrBuilder>( this, name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }

}
