package org.drools.lang.api;

import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.ImportDescr;

public class ImportDescrBuilderImpl extends BaseDescrBuilderImpl<ImportDescr>
    implements
    ImportDescrBuilder {

    protected ImportDescrBuilderImpl(boolean function) {
        super( function ? new FunctionImportDescr() : new ImportDescr() );
    }

    public ImportDescrBuilder target( String target ) {
        descr.setTarget( target );
        return this;
    }

}
