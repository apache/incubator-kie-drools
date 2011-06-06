package org.drools.lang.api.impl;

import org.drools.lang.api.DescrBuilder;
import org.drools.lang.api.EvalDescrBuilder;
import org.drools.lang.descr.EvalDescr;

public class EvalDescrBuilderImpl<P extends DescrBuilder<?>> extends BaseDescrBuilderImpl<EvalDescr>
    implements
    EvalDescrBuilder<P> {

    private P parent;

    public EvalDescrBuilderImpl() {
        super( new EvalDescr() );
    }

    public EvalDescrBuilderImpl<P> constraint( String expr ) {
        descr.setContent( expr );
        return this;
    }

    public P end() {
        return parent;
    }

}
