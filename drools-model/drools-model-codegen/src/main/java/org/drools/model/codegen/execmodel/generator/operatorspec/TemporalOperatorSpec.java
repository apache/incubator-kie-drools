package org.drools.model.codegen.execmodel.generator.operatorspec;

import org.drools.mvel.parser.ast.expr.PointFreeExpr;
import org.drools.mvel.parser.ast.expr.TemporalChunkExpr;
import org.drools.mvel.parser.ast.expr.TemporalLiteralChunkExpr;
import org.drools.mvel.parser.ast.expr.TemporalLiteralExpr;
import org.drools.mvel.parser.ast.expr.TemporalLiteralInfiniteChunkExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.TypedExpression;
import org.drools.model.codegen.execmodel.generator.expressiontyper.ExpressionTyper;

import static org.drools.model.codegen.execmodel.generator.DslMethodNames.DSL_NAMESPACE;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.NOT_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.createDslTopLevelMethod;

public class TemporalOperatorSpec implements OperatorSpec {
    public static final TemporalOperatorSpec INSTANCE = new TemporalOperatorSpec();

    public Expression getExpression(RuleContext context, PointFreeExpr pointFreeExpr, TypedExpression left, ExpressionTyper expressionTyper) {
        MethodCallExpr methodCallExpr = new MethodCallExpr( DSL_NAMESPACE.clone(), pointFreeExpr.getOperator().asString() );
        if (pointFreeExpr.getArg1() != null) {
            addArgumentToMethodCall( pointFreeExpr.getArg1(), methodCallExpr );
            if (pointFreeExpr.getArg2() != null) {
                addArgumentToMethodCall( pointFreeExpr.getArg2(), methodCallExpr );
            }
        }

        return pointFreeExpr.isNegated() ? createDslTopLevelMethod( NOT_CALL ).addArgument( methodCallExpr ) : methodCallExpr;
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
