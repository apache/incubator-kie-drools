package org.drools.lang.api;

import org.drools.lang.descr.PackageDescr;

public interface PackageDescrBuilder
    extends
    AttributeSupportBuilder<PackageDescr> {

    public PackageDescrBuilder name( String name );

    public ImportDescrBuilder newImport();

    public ImportDescrBuilder newFunctionImport();

    public GlobalDescrBuilder newGlobal();

    public DeclareDescrBuilder newDeclare();

    public FunctionDescrBuilder newFunction();

    public RuleDescrBuilder newRule();

    public QueryDescrBuilder newQuery();

}