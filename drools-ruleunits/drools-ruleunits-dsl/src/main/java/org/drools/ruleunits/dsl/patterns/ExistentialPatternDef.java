package org.drools.ruleunits.dsl.patterns;

import org.drools.model.Condition;
import org.drools.model.view.ViewItem;

import static org.drools.model.DSL.exists;
import static org.drools.model.DSL.not;

public class ExistentialPatternDef implements InternalPatternDef {

    private final Condition.Type type;
    private final InternalPatternDef pattern;

    public ExistentialPatternDef(Condition.Type type, InternalPatternDef pattern) {
        this.type = type;
        this.pattern = pattern;
    }

    @Override
    public ViewItem toExecModelItem() {
        if (type == Condition.Type.NOT) {
            return not(pattern.toExecModelItem());
        }
        if (type == Condition.Type.EXISTS) {
            return exists(pattern.toExecModelItem());
        }
        throw new UnsupportedOperationException();
    }
}
