package org.drools.modelcompiler.builder.generator.visitor.pattern;

import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.modelcompiler.builder.generator.ModelGenerator;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getPatternListenedProperties;

class SimpleConstraint implements DSLNode {

    private final RuleContext context;
    private final PatternDescr pattern;
    private final DrlxParseSuccess drlxParseResult;

    public SimpleConstraint(RuleContext context, PatternDescr pattern, DrlxParseSuccess drlxParseResult) {
        this.context = context;
        this.pattern = pattern;
        this.drlxParseResult = drlxParseResult;
    }

    @Override
    public void buildPattern() {
        // need to augment the watch inside drlxParseResult with the look-ahead properties.
        drlxParseResult.addAllWacthedProperties(context.getRuleDescr().lookAheadFieldsOfIdentifier(pattern));
        drlxParseResult.addAllWacthedProperties(getPatternListenedProperties(pattern));

        if (pattern.isUnification()) {
            drlxParseResult.setPatternBindingUnification(true);
        }

        ModelGenerator.processExpression(context, drlxParseResult);
    }
}
