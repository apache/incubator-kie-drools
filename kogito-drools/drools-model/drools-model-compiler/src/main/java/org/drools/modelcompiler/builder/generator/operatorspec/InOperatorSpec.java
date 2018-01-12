package org.drools.modelcompiler.builder.generator.operatorspec;

import org.drools.javaparser.ast.drlx.expr.PointFreeExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.generator.TypedExpression;

public class InOperatorSpec implements CustomOperatorSpec {
    public static final InOperatorSpec INSTANCE = new InOperatorSpec();

    public MethodCallExpr getMethodCallExpr(PointFreeExpr pointFreeExpr, TypedExpression left ) {
        MethodCallExpr asList = new MethodCallExpr(new NameExpr("java.util.Arrays"), "asList" );
        for (Expression rightExpr : pointFreeExpr.getRight()) {
            asList.addArgument( rightExpr );
        }
        MethodCallExpr methodCallExpr = new MethodCallExpr( asList, "contains" );
        methodCallExpr.addArgument( left.getExpression() );
        return methodCallExpr;
    }

    @Override
    public boolean isStatic() {
        return false;
    }
}
