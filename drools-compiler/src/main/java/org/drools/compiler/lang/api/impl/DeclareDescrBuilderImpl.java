package org.drools.compiler.lang.api.impl;

import org.drools.compiler.lang.api.DeclareDescrBuilder;
import org.drools.compiler.lang.api.EnumDeclarationDescrBuilder;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.TypeDeclarationDescrBuilder;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.api.EntryPointDeclarationDescrBuilder;
import org.drools.compiler.lang.api.WindowDeclarationDescrBuilder;

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
