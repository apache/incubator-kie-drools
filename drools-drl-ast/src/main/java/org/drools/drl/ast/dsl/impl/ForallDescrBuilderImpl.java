package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.DescrBuilder;
import org.drools.drl.ast.dsl.PatternDescrBuilder;
import org.drools.drl.ast.dsl.ForallDescrBuilder;
import org.drools.drl.ast.descr.ForallDescr;

/**
 * An implementation for the CEDescrBuilder
 */
public class ForallDescrBuilderImpl<P extends DescrBuilder< ?, ? >> extends BaseDescrBuilderImpl<P, ForallDescr>
    implements
    ForallDescrBuilder<P> {

    public ForallDescrBuilderImpl(P parent) {
        super( parent, new ForallDescr() );
    }

    /**
     * {@inheritDoc}
     */
    public PatternDescrBuilder<ForallDescrBuilder<P>> pattern( String type ) {
        PatternDescrBuilder<ForallDescrBuilder<P>> pattern = new PatternDescrBuilderImpl<>( this,
                                                                                                                 type );
        descr.addDescr( pattern.getDescr() );
        return pattern;
    }

    /**
     * {@inheritDoc}
     */
    public PatternDescrBuilder<ForallDescrBuilder<P>> pattern() {
        PatternDescrBuilder<ForallDescrBuilder<P>> pattern = new PatternDescrBuilderImpl<>( this );
        descr.addDescr( pattern.getDescr() );
        return pattern;
    }

    public P end() {
        return parent;
    }

}
