package org.drools.modelcompiler.builder.generator.operatorspec;

import org.drools.constraint.parser.ast.expr.PointFreeExpr;
import com.github.javaparser.ast.expr.Expression;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyper;

public interface OperatorSpec {
    Expression getExpression(RuleContext context, PointFreeExpr pointFreeExpr, TypedExpression left, ExpressionTyper expressionTyper);
    boolean isStatic();
}
