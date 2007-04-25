package org.drools.rule.builder;

import org.drools.lang.descr.BaseDescr;
import org.drools.rule.ConditionalElement;

public interface FromBuilder {

    /**
     * @inheritDoc
     */
    public ConditionalElement build(final RuleBuildContext context,
                                    final BaseDescr descr);

}