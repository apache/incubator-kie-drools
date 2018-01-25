package org.drools.modelcompiler.builder.generator.operatorspec;

import org.drools.javaparser.ast.drlx.expr.PointFreeExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.modelcompiler.builder.generator.TypedExpression;

public interface OperatorSpec {
    Expression getExpression( PointFreeExpr pointFreeExpr, TypedExpression left );
    boolean isStatic();
}
