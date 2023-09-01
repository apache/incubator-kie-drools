package org.drools.ruleunits.dsl.patterns;

import org.drools.model.Condition;
import org.drools.model.view.CombinedExprViewItem;
import org.drools.model.view.ViewItem;

public class CombinedPatternDef implements InternalPatternDef {

    private final Condition.Type type;
    private final InternalPatternDef[] patternDefs;

    public CombinedPatternDef(Condition.Type type, InternalPatternDef... patternDefs) {
        this.type = type;
        this.patternDefs = patternDefs;
    }

    @Override
    public ViewItem toExecModelItem() {
        ViewItem[] expressions = new ViewItem[patternDefs.length];
        for (int i = 0; i < patternDefs.length; i++) {
            expressions[i] = patternDefs[i].toExecModelItem();
        }
        return new CombinedExprViewItem(type, expressions);
    }
}
