package org.drools.drl.ast.dsl.impl;

import java.util.List;

import org.drools.drl.ast.dsl.BehaviorDescrBuilder;
import org.drools.drl.ast.dsl.PatternDescrBuilder;
import org.drools.drl.ast.descr.BehaviorDescr;

/**
 * A descr builder implementation for pattern behaviors
 */
public class BehaviorDescrBuilderImpl<P extends PatternDescrBuilder< ? >> extends BaseDescrBuilderImpl<P, BehaviorDescr>
    implements
    BehaviorDescrBuilder<P> {

    protected BehaviorDescrBuilderImpl(P parent) {
        super( parent, new BehaviorDescr() );
        this.parent.getDescr().addBehavior( descr );
    }

    public BehaviorDescrBuilder<P> type( String type,
                                         String subtype ) {
        descr.setType( type );
        descr.setSubType( subtype );
        return this;
    }

    public BehaviorDescrBuilder<P> parameters( List<String> params ) {
        descr.setParameters( params );
        return this;
    }
}
