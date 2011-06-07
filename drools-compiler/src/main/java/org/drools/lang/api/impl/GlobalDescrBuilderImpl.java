package org.drools.lang.api.impl;

import org.drools.lang.api.GlobalDescrBuilder;
import org.drools.lang.api.PackageDescrBuilder;
import org.drools.lang.descr.GlobalDescr;

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
