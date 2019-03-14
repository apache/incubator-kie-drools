package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;

public class AssignExprT extends TypedExpression {

    private final TypedExpression target;
    private final TypedExpression value;

    public AssignExprT(AssignExpr originalExpression, TypedExpression target, TypedExpression value) {
        super(originalExpression);
        this.target = target;
        this.value = value;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public Node toJavaExpression() {
        AssignExpr originalExpression = (AssignExpr) this.originalExpression;
        return new AssignExpr((Expression) target.toJavaExpression(),
                              (Expression) value.toJavaExpression(),
                              originalExpression.getOperator());
    }
}
