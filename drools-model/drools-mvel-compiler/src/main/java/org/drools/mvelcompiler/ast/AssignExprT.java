package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AssignExpr;

public class AssignExprT extends TypedExpression {

    public AssignExprT(AssignExpr originalExpression) {
        super(originalExpression);
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public Node toJavaExpression() {
        AssignExpr originalExpression = (AssignExpr) this.originalExpression;
        return new AssignExpr(originalExpression.getTarget(), originalExpression.getValue(), originalExpression.getOperator());
    }
}
