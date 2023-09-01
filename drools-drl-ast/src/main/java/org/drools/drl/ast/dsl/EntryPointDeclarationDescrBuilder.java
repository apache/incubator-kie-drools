package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.EntryPointDeclarationDescr;

/**
 * An interface for the entry point declaration descriptor builder
 */
public interface EntryPointDeclarationDescrBuilder
    extends
    AnnotatedDescrBuilder<EntryPointDeclarationDescrBuilder>, 
    DescrBuilder<PackageDescrBuilder, EntryPointDeclarationDescr> {

    /**
     * Declares the entry point id 
     * 
     * @param name the name of the entry point to be declared
     * 
     * @return itself
     */
    public EntryPointDeclarationDescrBuilder entryPointId( String name );

}
