package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.descr.EntryPointDescr;
import org.drools.drl.ast.descr.FromDescr;
import org.drools.drl.ast.descr.MVELExprDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.WindowReferenceDescr;
import org.drools.drl.ast.dsl.AccumulateDescrBuilder;
import org.drools.drl.ast.dsl.CollectDescrBuilder;
import org.drools.drl.ast.dsl.GroupByDescrBuilder;
import org.drools.drl.ast.dsl.PatternDescrBuilder;
import org.drools.drl.ast.dsl.SourceDescrBuilder;

/**
 * A descr builder implementation for pattern sources
 */
public class SourceDescrBuilderImpl<P extends PatternDescrBuilder<?>> extends BaseDescrBuilderImpl<P, PatternDescr>
    implements
    SourceDescrBuilder<P> {

    protected SourceDescrBuilderImpl(P parent) {
        super( parent, parent.getDescr() );
    }

    public P expression( String expression ) {
        FromDescr from = new FromDescr();
        from.setDataSource( new MVELExprDescr( expression ) );
        from.setResource(descr.getResource());
        descr.setSource( from );
        return parent;
    }

    public P entryPoint( String entryPoint ) {
        EntryPointDescr ep = new EntryPointDescr( entryPoint );
        ep.setResource(descr.getResource());
        descr.setSource( ep );
        return parent;
    }

    public CollectDescrBuilder<P> collect() {
        CollectDescrBuilder<P> collect = new CollectDescrBuilderImpl<>( parent );
        descr.setSource( collect.getDescr() );
        return collect;
    }

    public AccumulateDescrBuilder<P> accumulate() {
        AccumulateDescrBuilder<P> accumulate = new AccumulateDescrBuilderImpl<>( parent );
        descr.setSource( accumulate.getDescr() );
        return accumulate;
    }

    public GroupByDescrBuilder<P> groupBy() {
        GroupByDescrBuilder<P> accumulate = new GroupByDescrBuilderImpl<>( parent );
        descr.setSource( accumulate.getDescr() );
        return accumulate;
    }

    public P window( String window ) {
        WindowReferenceDescr wd = new WindowReferenceDescr( window );
        wd.setResource(descr.getResource());
        descr.setSource( wd );
        return parent;
    }
}
