package org.drools.lang.api;

import org.drools.lang.descr.ConditionalBranchDescr;

public interface ConditionalBranchDescrBuilder<P extends DescrBuilder< ? , ? >> extends DescrBuilder<P, ConditionalBranchDescr> {

    /**
     * Defines the condition for this conditional branch
     *
     * @return a descriptor builder for the EVAL CE
     */
    EvalDescrBuilder<ConditionalBranchDescrBuilder<P>> condition();

    /**
     * Defines a Consequence activated when the condition is evaluated to true
     *
     * @return a descriptor builder for the Named Consequence CE
     */
    NamedConsequenceDescrBuilder<ConditionalBranchDescrBuilder<P>> consequence();

    /**
     * Defines a else branch used when the condition is evaluated to false
     *
     * @return a descriptor builder for the else Conditional Branch CE
     */
    ConditionalBranchDescrBuilder<P> otherwise();
}
