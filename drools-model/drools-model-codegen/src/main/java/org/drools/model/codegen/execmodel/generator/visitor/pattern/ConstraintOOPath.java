package org.drools.model.codegen.execmodel.generator.visitor.pattern;

import java.util.Optional;

import org.drools.drl.ast.descr.PatternDescr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.generator.OOPathExprGenerator;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.visitor.DSLNode;

class ConstraintOOPath implements DSLNode {

    private final RuleContext context;
    private final PackageModel packageModel;
    private final PatternDescr pattern;
    private final Class<?> patternType;
    private final PatternConstraintParseResult patternConstraintParseResult;
    private final String expression;
    private final DrlxParseSuccess drlxParseResult;

    public ConstraintOOPath(RuleContext context, PackageModel packageModel, PatternDescr pattern, Class<?> patternType, PatternConstraintParseResult patternConstraintParseResult, DrlxParseSuccess drlxParseResult) {
        this.context = context;
        this.packageModel = packageModel;
        this.pattern = pattern;
        this.patternType = patternType;
        this.patternConstraintParseResult = patternConstraintParseResult;
        this.expression = patternConstraintParseResult.getExpression();
        this.drlxParseResult = drlxParseResult;
    }

    @Override
    public void buildPattern() {
        final String patternIdentifierGenerated;
        // If the  outer pattern does not have a binding we generate it
        if (patternConstraintParseResult.getPatternIdentifier() != null) {
            patternIdentifierGenerated = patternConstraintParseResult.getPatternIdentifier();
        } else {
            patternIdentifierGenerated = context.getExprId(patternType, expression);
            context.addDeclaration(patternIdentifierGenerated, patternType, Optional.of(pattern), Optional.empty());
        }

        new OOPathExprGenerator(context, packageModel).visit(patternType, patternIdentifierGenerated, drlxParseResult);
    }
}
