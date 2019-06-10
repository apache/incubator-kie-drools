package org.drools.modelcompiler.builder.generator.operatorspec;

import org.drools.mvel.parser.ast.expr.PointFreeExpr;
import org.drools.mvel.parser.ast.expr.TemporalChunkExpr;
import org.drools.mvel.parser.ast.expr.TemporalLiteralChunkExpr;
import org.drools.mvel.parser.ast.expr.TemporalLiteralExpr;
import org.drools.mvel.parser.ast.expr.TemporalLiteralInfiniteChunkExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
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
            TemporalChunkExpr firstTemporalExpression = ((TemporalLiteralExpr) expr).getChunks().iterator().next();
            if (firstTemporalExpression instanceof TemporalLiteralInfiniteChunkExpr) {
                methodCallExpr.addArgument( Long.MAX_VALUE + "L" );
                methodCallExpr.addArgument( "java.util.concurrent.TimeUnit.MILLISECONDS" );
            } else {
                final TemporalLiteralChunkExpr literal = ( TemporalLiteralChunkExpr ) firstTemporalExpression;
                methodCallExpr.addArgument( literal.getValue() + "L" );
                methodCallExpr.addArgument( "java.util.concurrent.TimeUnit." + literal.getTimeUnit() );
            }
        } else {
            methodCallExpr.addArgument( expr );
        }
    }
}
