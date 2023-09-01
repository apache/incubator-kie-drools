package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.GlobalDescrBuilder;
import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.descr.GlobalDescr;

public class GlobalDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, GlobalDescr>
    implements
        GlobalDescrBuilder {

    protected GlobalDescrBuilderImpl( PackageDescrBuilder parent ) {
        super( parent, new GlobalDescr() );
    }

    public GlobalDescrBuilder type( String type ) {
        descr.setType( type );
        return this;
    }

    public GlobalDescrBuilder identifier( String identifier ) {
        descr.setIdentifier( identifier );
        return this;
    }

}
