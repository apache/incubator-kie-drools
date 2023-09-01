package org.drools.model.codegen.execmodel.generator.visitor.pattern;

import org.drools.drl.ast.descr.PatternDescr;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.drlxparse.SingleDrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.expression.PatternExpressionBuilder;
import org.drools.model.codegen.execmodel.generator.visitor.DSLNode;

class PatternDSLSimpleConstraint implements DSLNode {

    private final RuleContext context;
    private final PatternDescr pattern;
    private final DrlxParseSuccess drlxParseResult;

    public PatternDSLSimpleConstraint(RuleContext context, PatternDescr pattern, DrlxParseSuccess drlxParseResult) {
        this.context = context;
        this.pattern = pattern;
        this.drlxParseResult = drlxParseResult;
    }

    @Override
    public void buildPattern() {
        if (pattern.isUnification()) {
            (( SingleDrlxParseSuccess ) drlxParseResult).setPatternBindingUnification(true);
        }

        new PatternExpressionBuilder(context).processExpression(drlxParseResult);
    }
}
