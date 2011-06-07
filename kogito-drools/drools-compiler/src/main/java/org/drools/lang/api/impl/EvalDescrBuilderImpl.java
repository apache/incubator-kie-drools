package org.drools.lang.api.impl;

import org.drools.lang.api.DescrBuilder;
import org.drools.lang.api.EvalDescrBuilder;
import org.drools.lang.descr.EvalDescr;

public class EvalDescrBuilderImpl<P extends DescrBuilder<?, ?>> extends BaseDescrBuilderImpl<P, EvalDescr>
    implements
    EvalDescrBuilder<P> {

    public EvalDescrBuilderImpl( P parent ) {
        super( parent, new EvalDescr() );
    }

    public EvalDescrBuilderImpl<P> constraint( String expr ) {
        descr.setContent( expr );
        return this;
    }

}
