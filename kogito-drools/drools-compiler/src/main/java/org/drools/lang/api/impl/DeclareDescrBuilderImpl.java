package org.drools.lang.api.impl;

import org.drools.lang.api.DeclareDescrBuilder;
import org.drools.lang.api.EntryPointDeclarationDescrBuilder;
import org.drools.lang.api.PackageDescrBuilder;
import org.drools.lang.api.TypeDeclarationDescrBuilder;
import org.drools.lang.api.WindowDeclarationDescrBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.api.EnumDeclarationDescrBuilder;

public class DeclareDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, PackageDescr>
    implements
    DeclareDescrBuilder {

    protected DeclareDescrBuilderImpl( PackageDescrBuilder parent ) {
        super( parent, parent.getDescr() );
    }

    public EntryPointDeclarationDescrBuilder entryPoint() {
        EntryPointDeclarationDescrBuilder epb = new EntryPointDeclarationDescrBuilderImpl( parent);
        descr.addEntryPointDeclaration( epb.getDescr() );
        return epb;
    }

    public TypeDeclarationDescrBuilder type() {
        TypeDeclarationDescrBuilder tddb = new TypeDeclarationDescrBuilderImpl( parent );
        descr.addTypeDeclaration( tddb.getDescr() );
        return tddb;
    }

    public WindowDeclarationDescrBuilder window() {
        WindowDeclarationDescrBuilder wddb = new WindowDeclarationDescrBuilderImpl( parent );
        descr.addWindowDeclaration( wddb.getDescr() );
        return wddb;
	}

    public EnumDeclarationDescrBuilder enumerative() {
        EnumDeclarationDescrBuilder eddb = new EnumDeclarationDescrBuilderImpl( parent );
        descr.addEnumDeclaration( eddb.getDescr() );
        return eddb;
    }


}
