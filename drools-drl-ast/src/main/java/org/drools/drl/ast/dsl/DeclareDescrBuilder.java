package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.PackageDescr;

/**
 *  A descriptor builder for declare statements
 *  
 */
public interface DeclareDescrBuilder
    extends
    DescrBuilder<PackageDescrBuilder, PackageDescr > {

    /**
     * Declares a new entry point
     * 
     * @return the descriptor builder for the entry point
     */
    public EntryPointDeclarationDescrBuilder entryPoint();

    /**
     * Declares a new type
     * 
     * @return the descriptor builder for the type
     */
    public TypeDeclarationDescrBuilder type();
    
    /**
     * Declares a new window
     * 
     * @return the descriptor builder for the window
     */
    public WindowDeclarationDescrBuilder window();


    /**
     * Declares a new enum
     *
     * @return the descriptor builder for the enum
     */
    public EnumDeclarationDescrBuilder enumerative();

}
