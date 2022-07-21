package org.drools.ruleunits.dsl.patterns;

import org.drools.model.view.ViewItem;

public interface InternalPatternDef extends PatternDef {
    ViewItem toExecModelItem();

    default InternalPatternDef subPatternFrom(InternalPatternDef from) {
        return this;
    }
}
