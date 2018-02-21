package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.javaparser.ast.drlx.OOPathExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.QueryGenerator;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getPatternListenedProperties;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getRootScope;

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
            context.popExprPointer();
        } else {
            if (!context.hasErrors()) {
                final List<PatternConstraintParseResult> patternConstraintParseResults = findAllConstraint(pattern, constraintDescrs, patternType);
                MethodCallExpr patternExpression = createPatternExpression(pattern);

                List<Expression> exprs = new ArrayList<>();
                context.pushExprPointer(exprs::add);
                buildConstraints(pattern, patternType, patternConstraintParseResults, allConstraintsPositional);
                context.popExprPointer();

                List<Expression> additionalPatterns = new ArrayList<>();
                for (Expression expr : exprs) {
                    MethodCallExpr currentExpr = ( MethodCallExpr ) expr;
                    if ( (( MethodCallExpr ) getRootScope( currentExpr )).getNameAsString().equals( "pattern" ) ) {
                        additionalPatterns.add( currentExpr );
                    } else {
                        (( MethodCallExpr ) expr).setScope( patternExpression );
                        patternExpression = currentExpr;
                    }
                }

                Collection<String> watchedProps = new ArrayList<>();
                watchedProps.addAll(context.getRuleDescr().lookAheadFieldsOfIdentifier(pattern));
                watchedProps.addAll(getPatternListenedProperties(pattern));
                if (!watchedProps.isEmpty()) {
                    patternExpression = new MethodCallExpr( patternExpression, "watch" );
                    watchedProps.stream().map( StringLiteralExpr::new ).forEach( patternExpression::addArgument );
                }

                context.addExpression( patternExpression );
                additionalPatterns.forEach( context::addExpression );
            }
        }
    }

    private MethodCallExpr createPatternExpression(PatternDescr pattern) {
        MethodCallExpr dslExpr = new MethodCallExpr(null, PATTERN_CALL);
        dslExpr.addArgument( QueryGenerator.substituteBindingWithQueryParameter(context, pattern.getIdentifier()) );
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
