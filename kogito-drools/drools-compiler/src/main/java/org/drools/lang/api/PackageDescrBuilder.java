package org.drools.lang.api;

import org.drools.lang.descr.PackageDescr;

public interface PackageDescrBuilder
    extends
    AttributeSupportBuilder<PackageDescrBuilder>,
    DescrBuilder<PackageDescrBuilder, PackageDescr> {

    /**
     * Sets the name of the package 
     * @param name
     * @return itself, in order to be used as a fluent API
     */
    public PackageDescrBuilder name( String name );

    /**
     * Adds an import statement to the package
     * @return the ImporDescrBuilder to set the import
     */
    public ImportDescrBuilder newImport();

    /**
     * Adds a function import statement to the package
     * @return the ImporDescrBuilder to set the function import
     */
    public ImportDescrBuilder newFunctionImport();

    /**
     * Adds a global statement to the package 
     * @return the GlobalDescrBuilder to set the global
     */
    public GlobalDescrBuilder newGlobal();

    /**
     * Adds a new Declare statement to the package 
     * @return the DeclareDescrBuilder to build the declare statement
     */
    public DeclareDescrBuilder newDeclare();

    /**
     * Adds a new Function statement to the package
     * @return the FunctionDescrBuilder to build the function statement
     */
    public FunctionDescrBuilder newFunction();

    /**
     * Adds a new Rule to the package
     * @return the RuleDescrBuilder to build the rule
     */
    public RuleDescrBuilder newRule();

    /**
     * Adds a new Query to the package
     * @return the QueryDescrBuilder to build the query
     */
    public QueryDescrBuilder newQuery();
    
    /**
     * Returns itself, as there is no container for a package 
     * descr builder.
     * 
     * {@inheritDoc}
     */
    public PackageDescrBuilder end();

}
