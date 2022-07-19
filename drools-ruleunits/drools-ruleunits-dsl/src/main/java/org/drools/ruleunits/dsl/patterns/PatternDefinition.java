package org.drools.ruleunits.dsl.patterns;

import org.drools.model.view.ViewItem;

public interface PatternDefinition {
    ViewItem toExecModelItem();
}
