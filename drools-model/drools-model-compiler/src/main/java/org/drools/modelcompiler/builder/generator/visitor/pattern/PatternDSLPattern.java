package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.util.List;
import java.util.Optional;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.javaparser.ast.drlx.OOPathExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;

class PatternDSLPattern extends PatternDSL {
    static final String PATTERN_CALL = "pattern";

    public PatternDSLPattern(RuleContext context, PackageModel packageModel, PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, Class<?> patternType, boolean allConstraintsPositional) {
        super(context, packageModel, pattern, constraintDescrs, allConstraintsPositional, patternType);
    }

    @Override
    public void buildPattern() {
        generatePatternIdentifierIfMissing();

        final Optional<Expression> declarationSource = buildFromDeclaration(pattern);
        context.addDeclaration(new DeclarationSpec(pattern.getIdentifier(), patternType, Optional.of(pattern), declarationSource));

        if (constraintDescrs.isEmpty() && !(pattern.getSource() instanceof AccumulateDescr)) {
            final MethodCallExpr patternExpression = createPatternExpression(pattern);
            context.addExpression(patternExpression);
            context.pushExprPointer(patternExpression::addArgument);
        } else {
            if (!context.hasErrors()) {
                final List<PatternConstraintParseResult> patternConstraintParseResults = findAllConstraint(pattern, constraintDescrs, patternType);
                final MethodCallExpr patternExpression = createPatternExpression(pattern);
                context.addExpression(patternExpression);
                context.pushExprPointer(patternExpression::addArgument);
                buildConstraints(pattern, patternType, patternConstraintParseResults, allConstraintsPositional);
            }
        }
        context.popExprPointer();
    }

    private MethodCallExpr createPatternExpression(PatternDescr pattern) {
        MethodCallExpr dslExpr = new MethodCallExpr(null, PATTERN_CALL);
        dslExpr.addArgument(new NameExpr(toVar(pattern.getIdentifier())));
        return dslExpr;
    }

    private void buildConstraints(PatternDescr pattern, Class<?> patternType, List<PatternConstraintParseResult> patternConstraintParseResults, boolean allConstraintsPositional) {
        for (PatternConstraintParseResult patternConstraintParseResult : patternConstraintParseResults) {
            buildConstraint(pattern, patternType, patternConstraintParseResult);
        }
    }

    private void buildConstraint(PatternDescr pattern, Class<?> patternType, PatternConstraintParseResult patternConstraintParseResult) {
        DrlxParseResult drlxParseResult1 = patternConstraintParseResult.getDrlxParseResult();
        String expression = patternConstraintParseResult.getExpression();

        drlxParseResult1.accept(drlxParseResult -> {
            DSLNode constraint;
            if (drlxParseResult.getExpr() instanceof OOPathExpr) {
                constraint = new ConstraintOOPath(context, packageModel, pattern, patternType, patternConstraintParseResult, expression, drlxParseResult);
            } else {
                constraint = new PatternDSLSimpleConstraint(context, pattern, drlxParseResult);
            }
            constraint.buildPattern();
        });
    }

}
