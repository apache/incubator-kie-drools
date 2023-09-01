package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.GlobalDescr;

/**
 *  A descriptor builder for Globals
 */
public interface GlobalDescrBuilder
    extends
    DescrBuilder<PackageDescrBuilder, GlobalDescr> {

    /**
     * Sets the type of the global. E.g.: java.util.List
     * 
     * @param type
     * 
     * @return itself
     */
    public GlobalDescrBuilder type( String type );

    /**
     * Sets the identifier for the global. E.g.: list
     * 
     * @param identifier
     * 
     * @return itself
     */
    public GlobalDescrBuilder identifier( String identifier );

}
