package org.drools.compiler.lang.api.impl;

import org.drools.compiler.lang.api.AccumulateImportDescrBuilder;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.descr.AccumulateImportDescr;

public class AccumulateImportDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, AccumulateImportDescr>
    implements
    AccumulateImportDescrBuilder {

    protected AccumulateImportDescrBuilderImpl(PackageDescrBuilder parent) {
        super( parent, new AccumulateImportDescr() );
    }

    public AccumulateImportDescrBuilder target( String target ) {
        descr.setTarget( target );
        return this;
    }

    public AccumulateImportDescrBuilder functionName(String functionName) {
        descr.setFunctionName( functionName );
        return this;
    }
    
}
