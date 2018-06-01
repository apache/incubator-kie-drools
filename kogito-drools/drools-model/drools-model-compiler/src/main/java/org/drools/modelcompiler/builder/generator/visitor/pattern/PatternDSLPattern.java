package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.QueryGenerator;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.findRootNodeViaScope;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getPatternListenedProperties;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.PATTERN_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.WATCH_CALL;

class PatternDSLPattern extends PatternDSL {

    public PatternDSLPattern(RuleContext context, PackageModel packageModel, PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, Class<?> patternType, boolean allConstraintsPositional) {
        super(context, packageModel, pattern, constraintDescrs, allConstraintsPositional, patternType);
    }

    @Override
    public void buildPattern() {
        initPattern();

        if (constraintDescrs.isEmpty() && !(pattern.getSource() instanceof AccumulateDescr)) {
            context.addExpression( addWatchToPattern( createPatternExpression(pattern) ) );
        } else {
            if (!context.hasErrors()) {
                final List<PatternConstraintParseResult> patternConstraintParseResults = findAllConstraint(pattern, constraintDescrs, patternType);
                MethodCallExpr patternExpression = createPatternExpression(pattern);

                List<Expression> exprs = new ArrayList<>();
                context.pushExprPointer(exprs::add);
                buildConstraints(pattern, patternType, patternConstraintParseResults);
                context.popExprPointer();

                List<Expression> additionalPatterns = new ArrayList<>();
                for (Expression expr : exprs) {
                    Optional<Expression> rootScope = findRootNodeViaScope( expr );
                    if ( rootScope.isPresent() && (( MethodCallExpr ) rootScope.get()).getNameAsString().equals( PATTERN_CALL ) ) {
                        additionalPatterns.add( expr );
                    } else {
                        MethodCallExpr currentExpr = ( MethodCallExpr ) expr;
                        (( MethodCallExpr ) expr).setScope( patternExpression );
                        patternExpression = currentExpr;
                    }
                }

                context.addExpression( addWatchToPattern( patternExpression ) );
                additionalPatterns.forEach( context::addExpression );
            }
        }
    }

    private MethodCallExpr addWatchToPattern( MethodCallExpr patternExpression ) {
        Collection<String> watchedProps = new ArrayList<>();
        watchedProps.addAll(context.getRuleDescr().lookAheadFieldsOfIdentifier(pattern));
        watchedProps.addAll(getPatternListenedProperties(pattern));
        if (!watchedProps.isEmpty()) {
            patternExpression = new MethodCallExpr( patternExpression, WATCH_CALL );
            watchedProps.stream().map( StringLiteralExpr::new ).forEach( patternExpression::addArgument );
        }
        return patternExpression;
    }

    private MethodCallExpr createPatternExpression(PatternDescr pattern) {
        MethodCallExpr dslExpr = new MethodCallExpr(null, PATTERN_CALL);
        dslExpr.addArgument( QueryGenerator.substituteBindingWithQueryParameter(context, pattern.getIdentifier()) );
        return dslExpr;
    }

    private void buildConstraints(PatternDescr pattern, Class<?> patternType, List<PatternConstraintParseResult> patternConstraintParseResults) {
        for (PatternConstraintParseResult patternConstraintParseResult : patternConstraintParseResults) {
            buildConstraint(pattern, patternType, patternConstraintParseResult);
        }
    }

    @Override
    protected DSLNode createSimpleConstraint( DrlxParseSuccess drlxParseResult, PatternDescr pattern ) {
        return new PatternDSLSimpleConstraint( context, pattern, drlxParseResult );
    }
}
