package org.drools.lang.api;

import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.ImportDescr;

public class ImportDescrBuilderImpl extends BaseDescrBuilderImpl
    implements
    ImportDescrBuilder {

    protected ImportDescrBuilderImpl(boolean function) {
        super( function ? new FunctionImportDescr() : new ImportDescr() );
    }

    public ImportDescr getDescr() {
        return (ImportDescr) descr;
    }

    public ImportDescrBuilder target( String target ) {
        ((ImportDescr) descr).setTarget( target );
        return this;
    }

}
