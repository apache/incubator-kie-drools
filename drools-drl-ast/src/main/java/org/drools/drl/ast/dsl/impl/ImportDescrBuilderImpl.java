package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.dsl.ImportDescrBuilder;
import org.drools.drl.ast.descr.FunctionImportDescr;
import org.drools.drl.ast.descr.ImportDescr;

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
