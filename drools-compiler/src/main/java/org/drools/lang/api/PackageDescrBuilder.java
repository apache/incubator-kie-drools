package org.drools.lang.api;

import org.drools.lang.descr.PackageDescr;

public interface PackageDescrBuilder
    extends
    DescrBuilder {

    public PackageDescr getDescr();

    public PackageDescrBuilder name( String name );

    public ImportDescrBuilder newImportDescr();

    public ImportDescrBuilder newFunctionImportDescr();

}