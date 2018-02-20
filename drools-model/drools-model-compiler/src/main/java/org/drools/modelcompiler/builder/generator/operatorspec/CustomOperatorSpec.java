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

public class CustomOperatorSpec implements OperatorSpec {
    public static final CustomOperatorSpec INSTANCE = new CustomOperatorSpec();

    public Expression getExpression( RuleContext context, PointFreeExpr pointFreeExpr, TypedExpression left ) {
        MethodCallExpr methodCallExpr = new MethodCallExpr( null, "eval" );

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
            if ( rightExpr instanceof LiteralExpr ) {
                methodCallExpr.addArgument( rightExpr );
            } else {
                TypedExpression typedExpression = DrlxParseUtil.toMethodCallWithClassCheck(context, rightExpr, null, null, context.getTypeResolver());
                methodCallExpr.addArgument( typedExpression.getExpression() );
            }
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
