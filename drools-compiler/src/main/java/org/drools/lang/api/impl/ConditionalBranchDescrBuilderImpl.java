package org.drools.lang.api.impl;

import org.drools.lang.api.ConditionalBranchDescrBuilder;
import org.drools.lang.api.DescrBuilder;
import org.drools.lang.api.EvalDescrBuilder;
import org.drools.lang.api.NamedConsequenceDescrBuilder;
import org.drools.lang.descr.ConditionalBranchDescr;

public class ConditionalBranchDescrBuilderImpl<P extends DescrBuilder< ?, ? >>
        extends BaseDescrBuilderImpl<P, ConditionalBranchDescr>
        implements ConditionalBranchDescrBuilder<P> {

    protected ConditionalBranchDescrBuilderImpl(final P parent) {
        super(parent, new ConditionalBranchDescr());
    }

    public EvalDescrBuilder<ConditionalBranchDescrBuilder<P>> condition() {
        EvalDescrBuilder<ConditionalBranchDescrBuilder<P>> eval = new EvalDescrBuilderImpl<ConditionalBranchDescrBuilder<P>>( this );
        getDescr().setCondition( eval.getDescr() );
        return eval;
    }

    public NamedConsequenceDescrBuilder<ConditionalBranchDescrBuilder<P>> consequence() {
        NamedConsequenceDescrBuilder<ConditionalBranchDescrBuilder<P>> namedConsequence = new NamedConsequenceDescrBuilderImpl<ConditionalBranchDescrBuilder<P>>( this );
        getDescr().setConsequence( namedConsequence.getDescr() );
        return namedConsequence;
    }

    public ConditionalBranchDescrBuilder<P> otherwise() {
        ConditionalBranchDescrBuilder<P> elseBranch = new ConditionalBranchDescrBuilderImpl<P>( parent );
        getDescr().setElseBranch(elseBranch.getDescr());
        return elseBranch;
    }
}
