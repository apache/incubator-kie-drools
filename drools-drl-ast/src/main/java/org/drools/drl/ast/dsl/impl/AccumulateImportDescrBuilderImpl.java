package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.AccumulateImportDescrBuilder;
import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.descr.AccumulateImportDescr;

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
