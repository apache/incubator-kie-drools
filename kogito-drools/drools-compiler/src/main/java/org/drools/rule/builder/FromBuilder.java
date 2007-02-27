package org.drools.rule.builder;

import org.drools.lang.descr.BaseDescr;
import org.drools.rule.ConditionalElement;

public interface FromBuilder {

    /**
     * @inheritDoc
     */
    public ConditionalElement build(final BuildContext context,
                                    final BuildUtils utils,
                                    final ColumnBuilder columnBuilder,
                                    final BaseDescr descr);

}