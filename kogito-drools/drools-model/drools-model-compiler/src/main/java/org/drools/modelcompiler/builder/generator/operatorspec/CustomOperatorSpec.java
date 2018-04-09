package org.drools.modelcompiler.builder.generator.operatorspec;

import org.drools.javaparser.ast.drlx.expr.PointFreeExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.LiteralExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.expr.UnaryExpr;
import org.drools.model.functions.Operator;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getLiteralExpressionType;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.EVAL_CALL;
import static org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser.coerceRightExpression;

public class CustomOperatorSpec implements OperatorSpec {
    public static final CustomOperatorSpec INSTANCE = new CustomOperatorSpec();

    public Expression getExpression( RuleContext context, PointFreeExpr pointFreeExpr, TypedExpression left ) {
        MethodCallExpr methodCallExpr = new MethodCallExpr( null, EVAL_CALL );

        String opName = pointFreeExpr.getOperator().asString();
        Operator operator = Operator.Register.getOperator( opName );
        try {
            // if the operator has an INSTANCE field avoid the operator lookup at runtime
            operator.getClass().getField( "INSTANCE" );
            methodCallExpr.addArgument( operator.getClass().getCanonicalName() + ".INSTANCE" );
        } catch (NoSuchFieldException e) {
            methodCallExpr.addArgument( new StringLiteralExpr( opName ) );
        }

        methodCallExpr.addArgument( left.getExpression() );
        for (Expression rightExpr : pointFreeExpr.getRight()) {
            TypedExpression right = rightExpr instanceof LiteralExpr ?
                    new TypedExpression( rightExpr, getLiteralExpressionType( (LiteralExpr) rightExpr ) ) :
                    DrlxParseUtil.toMethodCallWithClassCheck(context, rightExpr, null, null, context.getTypeResolver());
            if (operator.requiresCoercion()) {
                coerceRightExpression( left, right );
            }
            methodCallExpr.addArgument( right.getExpression() );
        }

        return pointFreeExpr.isNegated() ?
                new UnaryExpr( methodCallExpr, UnaryExpr.Operator.LOGICAL_COMPLEMENT ) :
                methodCallExpr;
    }

    @Override
    public boolean isStatic() {
        return false;
    }
}
