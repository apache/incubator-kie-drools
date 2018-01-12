package org.drools.modelcompiler.builder.generator.operatorspec;

import org.drools.javaparser.ast.drlx.expr.PointFreeExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.modelcompiler.builder.generator.TypedExpression;

public interface CustomOperatorSpec {
    MethodCallExpr getMethodCallExpr(PointFreeExpr pointFreeExpr, TypedExpression left );
    boolean isStatic();
}
