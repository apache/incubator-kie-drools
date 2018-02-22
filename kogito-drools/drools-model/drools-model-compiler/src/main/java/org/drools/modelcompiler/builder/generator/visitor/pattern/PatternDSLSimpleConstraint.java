package org.drools.modelcompiler.builder.generator.visitor.pattern;

import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.expression.PatternExpressionBuilder;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;

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
            drlxParseResult.setPatternBindingUnification(true);
        }

        new PatternExpressionBuilder(context).processExpression(drlxParseResult);
    }
}
