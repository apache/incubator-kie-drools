package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.AccumulateImportDescr;

/**
 * An interface for the import builder
 */
public interface AccumulateImportDescrBuilder
    extends
    DescrBuilder<PackageDescrBuilder, AccumulateImportDescr> {

    /**
     * Sets the import target
     * 
     * @param target the class or package being imported
     * @return itself
     */
    public AccumulateImportDescrBuilder target( String target );
    
    /**
     * Sets the function name for the accumulate import
     * 
     * @param functionName the function name
     * @return itself
     */
    public AccumulateImportDescrBuilder functionName( String functionName );

}
