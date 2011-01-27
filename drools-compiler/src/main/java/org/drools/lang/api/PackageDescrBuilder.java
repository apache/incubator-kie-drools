package org.drools.lang.api;

import org.drools.lang.descr.PackageDescr;

public interface PackageDescrBuilder
    extends
    DescrBuilder<PackageDescr>,
    AttributeSupportBuilder {

    public PackageDescrBuilder name( String name );

    public ImportDescrBuilder newImport();

    public ImportDescrBuilder newFunctionImport();

    public GlobalDescrBuilder newGlobal();

    public DeclareDescrBuilder newDeclare();

    public RuleDescrBuilder newRule();

}