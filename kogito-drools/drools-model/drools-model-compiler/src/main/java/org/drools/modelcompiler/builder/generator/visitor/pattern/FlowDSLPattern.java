package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseFail;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.ParseResultVisitor;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;
import org.drools.mvel.parser.ast.expr.OOPathExpr;

import static org.drools.modelcompiler.builder.generator.DslMethodNames.AND_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.INPUT_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.WATCH_CALL;

class FlowDSLPattern extends PatternDSL {

    private final boolean allConstraintsPositional;

    public FlowDSLPattern(RuleContext context, PackageModel packageModel, PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, Class<?> patternType, boolean allConstraintsPositional) {
        super(context, packageModel, pattern, constraintDescrs, patternType);
        this.allConstraintsPositional = allConstraintsPositional;
    }

    @Override
    protected void buildPattern(DeclarationSpec declarationSpec, List<PatternConstraintParseResult> patternConstraintParseResults) {
        if(shouldAddInputPattern(patternConstraintParseResults)) {
            context.addExpression(input(declarationSpec));
        }
        buildConstraints(pattern, patternType, patternConstraintParseResults, allConstraintsPositional);
    }

    @Override
    public MethodCallExpr input(DeclarationSpec declarationSpec) {
        return createInputExpression(pattern, declarationSpec);
    }

    private boolean shouldAddInputPattern(List<PatternConstraintParseResult> parseResults) {
        final Predicate<? super PatternConstraintParseResult> hasOneOOPathExpr = (Predicate<PatternConstraintParseResult>) patternConstraintParseResult ->
            patternConstraintParseResult.getDrlxParseResult().acceptWithReturnValue(new ParseResultVisitor<Boolean>() {
                @Override
                public Boolean onSuccess(DrlxParseSuccess drlxParseResult) {
                    return drlxParseResult.getExpr() instanceof OOPathExpr;
                }

                @Override
                public Boolean onFail(DrlxParseFail failure) {
                    return false;
                }
            });

        return parseResults
                .stream()
                .anyMatch(hasOneOOPathExpr);
    }

    private MethodCallExpr createInputExpression(PatternDescr pattern, DeclarationSpec declarationSpec) {
        MethodCallExpr exprDSL = new MethodCallExpr(null, INPUT_CALL);
        exprDSL.addArgument( context.getVarExpr( pattern.getIdentifier()) );
        if (context.isQuery()) {
            Optional<Expression> declarationSource = declarationSpec.getDeclarationSource();
            declarationSource.ifPresent(exprDSL::addArgument);
        }

        if (context.isPropertyReactive(patternType)) {
            Set<String> settableWatchedProps = getSettableWatchedProps();
            if ( !settableWatchedProps.isEmpty() ) {
                exprDSL = new MethodCallExpr( exprDSL, WATCH_CALL );
                settableWatchedProps.stream()
                        .map( StringLiteralExpr::new )
                        .forEach( exprDSL::addArgument );
            }
        }

        return exprDSL;
    }

    private void buildConstraints(PatternDescr pattern, Class<?> patternType, List<PatternConstraintParseResult> patternConstraintParseResults, boolean allConstraintsPositional) {
        if (allConstraintsPositional) {
            final MethodCallExpr andDSL = new MethodCallExpr(null, AND_CALL);
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

    @Override
    protected DSLNode createSimpleConstraint( DrlxParseSuccess drlxParseResult, PatternDescr pattern ) {
        return new FlowDSLSimpleConstraint( context, pattern, drlxParseResult );
    }

}
