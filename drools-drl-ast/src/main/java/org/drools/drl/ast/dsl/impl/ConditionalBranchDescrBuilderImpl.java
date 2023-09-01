package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.ConditionalBranchDescrBuilder;
import org.drools.drl.ast.dsl.DescrBuilder;
import org.drools.drl.ast.dsl.NamedConsequenceDescrBuilder;
import org.drools.drl.ast.dsl.EvalDescrBuilder;
import org.drools.drl.ast.descr.ConditionalBranchDescr;

public class ConditionalBranchDescrBuilderImpl<P extends DescrBuilder< ?, ? >>
        extends BaseDescrBuilderImpl<P, ConditionalBranchDescr>
        implements ConditionalBranchDescrBuilder<P> {

    protected ConditionalBranchDescrBuilderImpl(final P parent) {
        super(parent, new ConditionalBranchDescr());
    }

    public EvalDescrBuilder<ConditionalBranchDescrBuilder<P>> condition() {
        EvalDescrBuilder<ConditionalBranchDescrBuilder<P>> eval = new EvalDescrBuilderImpl<>( this );
        getDescr().setCondition( eval.getDescr() );
        return eval;
    }

    public NamedConsequenceDescrBuilder<ConditionalBranchDescrBuilder<P>> consequence() {
        NamedConsequenceDescrBuilder<ConditionalBranchDescrBuilder<P>> namedConsequence = new NamedConsequenceDescrBuilderImpl<>( this );
        getDescr().setConsequence( namedConsequence.getDescr() );
        return namedConsequence;
    }

    public ConditionalBranchDescrBuilder<P> otherwise() {
        ConditionalBranchDescrBuilder<P> elseBranch = new ConditionalBranchDescrBuilderImpl<>( parent );
        getDescr().setElseBranch(elseBranch.getDescr());
        return elseBranch;
    }
}
