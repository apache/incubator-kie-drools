package org.drools.rule.builder;

import org.drools.lang.descr.BaseDescr;
import org.drools.rule.ConditionalElement;
import org.drools.rule.builder.dialect.java.BuildUtils;

public interface AccumulateBuilder {

    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.ConditionalElementBuilder#build(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.semantics.java.builder.ColumnBuilder, org.drools.lang.descr.BaseDescr)
     */
    public ConditionalElement build(BuildContext context,
                                    BuildUtils utils,
                                    ColumnBuilder columnBuilder,
                                    BaseDescr descr);

}