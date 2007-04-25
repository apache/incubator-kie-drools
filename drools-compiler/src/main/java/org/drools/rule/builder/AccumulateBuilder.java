package org.drools.rule.builder;

import org.drools.lang.descr.BaseDescr;
import org.drools.rule.ConditionalElement;

public interface AccumulateBuilder {

    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.ConditionalElementBuilder#build(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.semantics.java.builder.PatternBuilder, org.drools.lang.descr.BaseDescr)
     */
    public ConditionalElement build(RuleBuildContext context,
                                    BaseDescr descr);

}