package org.drools.compiler.lang.api.impl;

import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.ImportDescrBuilder;
import org.drools.compiler.lang.descr.FunctionImportDescr;
import org.drools.compiler.lang.descr.ImportDescr;

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
