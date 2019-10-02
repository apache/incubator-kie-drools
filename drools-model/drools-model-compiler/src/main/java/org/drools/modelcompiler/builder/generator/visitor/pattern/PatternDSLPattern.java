package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.findLastMethodInChain;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.findRootNodeViaScope;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.PATTERN_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.WATCH_CALL;

class PatternDSLPattern extends PatternDSL {

    protected PatternDSLPattern(RuleContext context, PackageModel packageModel, PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, Class<?> patternType) {
        super(context, packageModel, pattern, constraintDescrs, patternType);
    }

    @Override
    protected void buildPattern(DeclarationSpec declarationSpec, List<PatternConstraintParseResult> patternConstraintParseResults) {
        MethodCallExpr patternExpression = createPatternExpression(pattern, declarationSpec);

        List<Expression> exprs = new ArrayList<>();
        context.pushExprPointer(exprs::add);
        buildConstraints(pattern, patternType, patternConstraintParseResults);
        context.popExprPointer();

        List<Expression> additionalPatterns = new ArrayList<>();
        for (Expression expr : exprs) {
            Optional<Expression> rootScope = findRootNodeViaScope(expr );
            if ( rootScope.isPresent() && (( MethodCallExpr ) rootScope.get()).getNameAsString().equals( PATTERN_CALL ) ) {
                additionalPatterns.add( expr );
            } else {
                MethodCallExpr currentExpr = ( MethodCallExpr ) expr;
                findLastMethodInChain( currentExpr ).setScope( patternExpression );
                patternExpression = currentExpr;
            }
        }

        context.addExpression( addWatchToPattern( patternExpression ) );
        additionalPatterns.forEach( context::addExpression );
    }

    @Override
    public MethodCallExpr input(DeclarationSpec declarationSpec) {
        return addWatchToPattern( createPatternExpression(pattern, declarationSpec) );
    }

    private MethodCallExpr addWatchToPattern( MethodCallExpr patternExpression ) {
        if (context.isPropertyReactive(patternType)) {
            Set<String> settableWatchedProps = getSettableWatchedProps();
            if ( !settableWatchedProps.isEmpty() ) {
                patternExpression = new MethodCallExpr( patternExpression, WATCH_CALL );
                settableWatchedProps.stream().map( StringLiteralExpr::new ).forEach( patternExpression::addArgument );
            }
        }
        return patternExpression;
    }

    private MethodCallExpr createPatternExpression(PatternDescr pattern, DeclarationSpec declarationSpec) {
        MethodCallExpr dslExpr = new MethodCallExpr(null, PATTERN_CALL);
        dslExpr.addArgument( context.getVarExpr( pattern.getIdentifier()) );
        if (context.isQuery()) {
            Optional<Expression> declarationSource = declarationSpec.getDeclarationSource();
            declarationSource.ifPresent(dslExpr::addArgument);
        }
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
