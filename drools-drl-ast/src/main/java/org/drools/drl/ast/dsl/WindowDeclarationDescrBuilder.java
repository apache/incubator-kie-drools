package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.WindowDeclarationDescr;

/**
 *  A descriptor builder for declared types
 */
public interface WindowDeclarationDescrBuilder
    extends
    AnnotatedDescrBuilder<WindowDeclarationDescrBuilder>,
    DescrBuilder<PackageDescrBuilder, WindowDeclarationDescr>,
    PatternContainerDescrBuilder<WindowDeclarationDescrBuilder, WindowDeclarationDescr>{

    /**
     * Defines the window name
     *  
     * @param name the window name
     * 
     * @return itself
     */
    public WindowDeclarationDescrBuilder name( String name );

}
