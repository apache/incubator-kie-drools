package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.DescrBuilder;
import org.drools.drl.ast.dsl.EvalDescrBuilder;
import org.drools.drl.ast.descr.EvalDescr;

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
