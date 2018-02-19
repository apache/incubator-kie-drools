package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseFail;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.ParseResultVisitor;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.visitor.pattern.PatternVisitor.INPUT_CALL;

class FlowDSLPattern extends PatternDSL {

    public FlowDSLPattern(RuleContext context, PackageModel packageModel, PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, Class<?> patternType, boolean allConstraintsPositional) {
        super(context, packageModel, pattern, constraintDescrs, allConstraintsPositional, patternType);
    }

    @Override
    public void buildPattern() {
        generatePatternIdentifierIfMissing();

        final Optional<Expression> declarationSource = buildFromDeclaration(pattern);
        context.addDeclaration(new DeclarationSpec(pattern.getIdentifier(), patternType, Optional.of(pattern), declarationSource));

        if (constraintDescrs.isEmpty() && !(pattern.getSource() instanceof AccumulateDescr)) {
            context.addExpression(createInputExpression(pattern));
        } else {
            if (!context.hasErrors()) {
                final List<PatternConstraintParseResult> patternConstraintParseResults = findAllConstraint(pattern, constraintDescrs, patternType);
                if(shouldAddInputPattern(patternConstraintParseResults)) {
                    context.addExpression(createInputExpression(pattern));
                }
                buildConstraints(pattern, patternType, patternConstraintParseResults, allConstraintsPositional);
            }
        }
    }

    private boolean shouldAddInputPattern(List<PatternConstraintParseResult> parseResults) {
        final Predicate<? super PatternConstraintParseResult> hasOneOOPathExpr = (Predicate<PatternConstraintParseResult>) patternConstraintParseResult -> {
            return patternConstraintParseResult.getDrlxParseResult().acceptWithReturnValue(new ParseResultVisitor<Boolean>() {
                @Override
                public Boolean onSuccess(DrlxParseSuccess drlxParseResult) {
                    return drlxParseResult.getExpr() instanceof OOPathExpr;
                }

                @Override
                public Boolean onFail(DrlxParseFail failure) {
                    return false;
                }
            });
        };

        return parseResults
                .stream()
                .anyMatch(hasOneOOPathExpr);
    }

    private MethodCallExpr createInputExpression(PatternDescr pattern) {
        MethodCallExpr dslExpr = new MethodCallExpr(null, INPUT_CALL);
        dslExpr.addArgument(new NameExpr(toVar(pattern.getIdentifier())));
        return dslExpr;
    }

    public List<PatternConstraintParseResult> findAllConstraint(PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, Class<?> patternType) {
        final List<PatternConstraintParseResult> patternConstraintParseResults = new ArrayList<>();
        for (BaseDescr constraint : constraintDescrs) {
            final PatternConstraintParseResult patternConstraintParseResult = parseConstraint(pattern, patternType, constraint);
            patternConstraintParseResults.add(patternConstraintParseResult);
        }
        return patternConstraintParseResults;
    }

    private void buildConstraints(PatternDescr pattern, Class<?> patternType, List<PatternConstraintParseResult> patternConstraintParseResults, boolean allConstraintsPositional) {
        if (allConstraintsPositional) {
            final MethodCallExpr andDSL = new MethodCallExpr(null, "and");
            context.addExpression(andDSL);
            context.pushExprPointer(andDSL::addArgument);
        }

        for (PatternConstraintParseResult patternConstraintParseResult : patternConstraintParseResults) {
            buildConstraint(pattern, patternType, patternConstraintParseResult);
        }
        if (allConstraintsPositional) {
            context.popExprPointer();
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
                constraint = new SimpleConstraint(context, pattern, drlxParseResult);
            }
            constraint.buildPattern();
        });
    }
}
