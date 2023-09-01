package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.ImportDescr;

/**
 * An interface for the import builder
 */
public interface ImportDescrBuilder
    extends
    DescrBuilder<PackageDescrBuilder, ImportDescr> {

    /**
     * Sets the import target
     * 
     * @param target the class or package being imported
     * @return itself
     */
    public ImportDescrBuilder target( String target );

}
