package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.DescrBuilder;
import org.drools.drl.ast.dsl.CollectDescrBuilder;
import org.drools.drl.ast.dsl.PatternDescrBuilder;
import org.drools.drl.ast.descr.CollectDescr;

/**
 * An implementation for the CollectDescrBuilder
 */
public class CollectDescrBuilderImpl<P extends DescrBuilder< ?, ? >> extends BaseDescrBuilderImpl<P, CollectDescr>
    implements
    CollectDescrBuilder<P> {

    public CollectDescrBuilderImpl(P parent) {
        super( parent, new CollectDescr() );
    }

    /**
     * {@inheritDoc}
     */
    public PatternDescrBuilder<CollectDescrBuilder<P>> pattern( String type ) {
        PatternDescrBuilder<CollectDescrBuilder<P>> pattern = new PatternDescrBuilderImpl<>( this,
                                                                                                                   type );
        descr.setInputPattern( pattern.getDescr() );
        return pattern;
    }

    /**
     * {@inheritDoc}
     */
    public PatternDescrBuilder<CollectDescrBuilder<P>> pattern() {
        PatternDescrBuilder<CollectDescrBuilder<P>> pattern = new PatternDescrBuilderImpl<>( this );
        descr.setInputPattern( pattern.getDescr() );
        return pattern;
    }

}
