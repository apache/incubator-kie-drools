package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.util.Collection;

import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.modelcompiler.builder.generator.IndexIdGenerator;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.expression.PatternExpressionBuilder;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;

class PatternDSLSimpleConstraint implements DSLNode {

    private static final IndexIdGenerator indexIdGenerator = new IndexIdGenerator();



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
        // need to augment the reactOn inside drlxParseResult with the look-ahead properties.
        Collection<String> lookAheadFieldsOfIdentifier = context.getRuleDescr().lookAheadFieldsOfIdentifier(pattern);
        drlxParseResult.getReactOnProperties().addAll(lookAheadFieldsOfIdentifier);
        drlxParseResult.setWatchedProperties(getPatternListenedProperties(pattern));

        if (pattern.isUnification()) {
            drlxParseResult.setPatternBindingUnification(true);
        }

        new PatternExpressionBuilder(context).processExpression(drlxParseResult);
    }


    private static String[] getPatternListenedProperties(PatternDescr pattern) {
        AnnotationDescr watchAnn = pattern != null ? pattern.getAnnotation("watch") : null;
        return watchAnn == null ? new String[0] : watchAnn.getValue().toString().split(",");
    }


}
