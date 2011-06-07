package org.drools.lang.api.impl;

import org.drools.lang.api.ImportDescrBuilder;
import org.drools.lang.api.PackageDescrBuilder;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.ImportDescr;

public class ImportDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, ImportDescr>
    implements
    ImportDescrBuilder {

    protected ImportDescrBuilderImpl(PackageDescrBuilder parent, boolean function) {
        super( parent, function ? new FunctionImportDescr() : new ImportDescr() );
    }

    public ImportDescrBuilder target( String target ) {
        descr.setTarget( target );
        return this;
    }

}
