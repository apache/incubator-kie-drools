package org.drools.lang.api.impl;

import org.drools.lang.api.ImportDescrBuilder;
import org.drools.lang.api.PackageDescrBuilder;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.ImportDescr;

public class ImportDescrBuilderImpl extends BaseDescrBuilderImpl<ImportDescr>
    implements
    ImportDescrBuilder {

    private PackageDescrBuilder parent;

    protected ImportDescrBuilderImpl(PackageDescrBuilder parent, boolean function) {
        super( function ? new FunctionImportDescr() : new ImportDescr() );
        this.parent = parent;
    }

    public PackageDescrBuilder target( String target ) {
        descr.setTarget( target );
        return parent;
    }

}
