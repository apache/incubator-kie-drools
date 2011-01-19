package org.drools.lang.api;

import org.drools.lang.descr.GlobalDescr;

public class GlobalDescrBuilderImpl extends BaseDescrBuilderImpl
    implements
    GlobalDescrBuilder {

    protected GlobalDescrBuilderImpl() {
        super( new GlobalDescr() );
    }

    public GlobalDescr getDescr() {
        return (GlobalDescr) descr;
    }

    public GlobalDescrBuilder type( String type ) {
        ((GlobalDescr) descr).setType( type );
        return this;
    }

    public GlobalDescrBuilder identifier( String identifier ) {
        ((GlobalDescr) descr).setIdentifier( identifier );
        return this;
    }

}
