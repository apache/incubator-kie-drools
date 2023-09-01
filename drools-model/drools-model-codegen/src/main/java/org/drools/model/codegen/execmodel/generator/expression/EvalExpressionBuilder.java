package org.drools.model.codegen.execmodel.generator.expression;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.drlxparse.SingleDrlxParseSuccess;

import static org.drools.model.codegen.execmodel.generator.DslMethodNames.EVAL_EXPR_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.createDslTopLevelMethod;

public class EvalExpressionBuilder extends AbstractExpressionBuilder {

    public EvalExpressionBuilder( RuleContext context) {
        super(context);
    }

    @Override
    public void processExpression(SingleDrlxParseSuccess drlxParseResult)  {
        if (drlxParseResult.hasUnificationVariable()) {
            Expression dslExpr = buildUnificationExpression(drlxParseResult);
            context.addExpression(dslExpr);
        } else if ( drlxParseResult.isPredicate() ) {
            Expression dslExpr = buildSingleExpressionWithIndexing(drlxParseResult);
            context.addExpression(dslExpr);
        }
    }

    private MethodCallExpr buildSingleExpressionWithIndexing(SingleDrlxParseSuccess drlxParseResult) {
        String exprId = createExprId(drlxParseResult);
        MethodCallExpr exprDSL = createDslTopLevelMethod(EVAL_EXPR_CALL);
        if (exprId != null && !"".equals(exprId)) {
            exprDSL.addArgument( new StringLiteralExpr(exprId) );
        }

        return buildExpression(drlxParseResult, exprDSL );
    }

    private MethodCallExpr buildExpression(SingleDrlxParseSuccess drlxParseResult, MethodCallExpr exprDSL ) {
        if (drlxParseResult.isTemporal()) {
            return buildTemporalExpression(drlxParseResult, exprDSL);
        }

        final List<String> usedDeclarationsWithUnification = new ArrayList<>();
        if( drlxParseResult.isPatternBindingUnification() ) {
            usedDeclarationsWithUnification.add(drlxParseResult.getPatternBinding());
        } else {
            if (drlxParseResult.getPatternBinding() != null) {
                exprDSL.addArgument(context.getVarExpr(drlxParseResult.getPatternBinding()));
            }
        }

        usedDeclarationsWithUnification.addAll(drlxParseResult.getUsedDeclarations());
        usedDeclarationsWithUnification.stream()
                .filter( s -> !(drlxParseResult.isSkipThisAsParam() && s.equals( drlxParseResult.getPatternBinding() ) ) )
                .map(context::getVarExpr)
                .forEach(exprDSL::addArgument);

        if (drlxParseResult.getRightLiteral() != null) {
            exprDSL.addArgument( "" + drlxParseResult.getRightLiteral() );
        }

        exprDSL.addArgument(buildConstraintExpression( drlxParseResult, drlxParseResult.getExpr() ));
        return exprDSL;
    }

    @Override
    protected MethodCallExpr buildTemporalExpression(SingleDrlxParseSuccess drlxParseResult, MethodCallExpr exprDSL) {
        if (drlxParseResult.getPatternBinding() != null) {
            exprDSL.addArgument(context.getVarExpr(drlxParseResult.getPatternBinding()));
        }
        return super.buildTemporalExpression(drlxParseResult, exprDSL);
    }

    @Override
    public MethodCallExpr buildExpressionWithIndexing(DrlxParseSuccess drlxParseResult) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MethodCallExpr buildBinding(SingleDrlxParseSuccess drlxParseResult) {
        throw new UnsupportedOperationException();
    }
}
