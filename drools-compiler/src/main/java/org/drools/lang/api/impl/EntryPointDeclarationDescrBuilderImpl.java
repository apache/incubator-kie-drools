package org.drools.lang.api.impl;

import org.drools.lang.api.EntryPointDeclarationDescrBuilder;
import org.drools.lang.api.PackageDescrBuilder;
import org.drools.lang.descr.EntryPointDeclarationDescr;

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

}
