package org.drools.modelcompiler.builder.generator.operatorspec;

import org.drools.javaparser.ast.drlx.expr.PointFreeExpr;
import org.drools.javaparser.ast.drlx.expr.TemporalLiteralChunkExpr;
import org.drools.javaparser.ast.drlx.expr.TemporalLiteralExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyper;

import static org.drools.modelcompiler.builder.generator.DslMethodNames.NOT_CALL;

public class TemporalOperatorSpec implements OperatorSpec {
    public static final TemporalOperatorSpec INSTANCE = new TemporalOperatorSpec();

    public Expression getExpression(RuleContext context, PointFreeExpr pointFreeExpr, TypedExpression left, ExpressionTyper expressionTyper) {
        MethodCallExpr methodCallExpr = new MethodCallExpr( null, "D." + pointFreeExpr.getOperator().asString() );
        if (pointFreeExpr.getArg1() != null) {
            addArgumentToMethodCall( pointFreeExpr.getArg1(), methodCallExpr );
            if (pointFreeExpr.getArg2() != null) {
                addArgumentToMethodCall( pointFreeExpr.getArg2(), methodCallExpr );
            }
        }

        return pointFreeExpr.isNegated() ? new MethodCallExpr( null, NOT_CALL ).addArgument( methodCallExpr ) : methodCallExpr;
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    public static void addArgumentToMethodCall(Expression expr, MethodCallExpr methodCallExpr ) {
        if (expr instanceof TemporalLiteralExpr) {
            TemporalLiteralExpr tempExpr1 = (TemporalLiteralExpr) expr;
            final TemporalLiteralChunkExpr firstTemporalExpression = (TemporalLiteralChunkExpr) tempExpr1.getChunks().iterator().next();
            methodCallExpr.addArgument("" + firstTemporalExpression.getValue() );
            methodCallExpr.addArgument( "java.util.concurrent.TimeUnit." + firstTemporalExpression.getTimeUnit() );
        } else {
            methodCallExpr.addArgument( expr );
        }
    }
}
