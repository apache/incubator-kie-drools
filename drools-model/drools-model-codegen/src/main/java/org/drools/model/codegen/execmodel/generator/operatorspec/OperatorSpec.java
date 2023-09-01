package org.drools.model.codegen.execmodel.generator.operatorspec;

import org.drools.mvel.parser.ast.expr.PointFreeExpr;
import com.github.javaparser.ast.expr.Expression;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.TypedExpression;
import org.drools.model.codegen.execmodel.generator.expressiontyper.ExpressionTyper;

public interface OperatorSpec {
    Expression getExpression(RuleContext context, PointFreeExpr pointFreeExpr, TypedExpression left, ExpressionTyper expressionTyper);
    boolean isStatic();
}
