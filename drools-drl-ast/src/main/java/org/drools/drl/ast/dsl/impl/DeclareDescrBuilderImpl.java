package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.DeclareDescrBuilder;
import org.drools.drl.ast.dsl.EnumDeclarationDescrBuilder;
import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.dsl.TypeDeclarationDescrBuilder;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.dsl.EntryPointDeclarationDescrBuilder;
import org.drools.drl.ast.dsl.WindowDeclarationDescrBuilder;

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
