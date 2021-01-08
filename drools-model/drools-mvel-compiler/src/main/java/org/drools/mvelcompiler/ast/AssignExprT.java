package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;

public class AssignExprT implements TypedExpression {

    private final AssignExpr.Operator operator;
    private final TypedExpression target;
    private final TypedExpression value;

    public AssignExprT(AssignExpr.Operator operator, TypedExpression target, TypedExpression value) {
        this.operator = operator;
        this.target = target;
        this.value = value;
    }

    @Override
    public Optional<Type> getType() {
        return target.getType();
    }

    @Override
    public Node toJavaExpression() {
        return new AssignExpr((Expression) target.toJavaExpression(),
                              (Expression) value.toJavaExpression(),
                              operator);
    }

    @Override
    public String toString() {
        return "AssignExprT\n{" +
                "\ttarget=" + target +
                ",\t value=" + value +
                '}';
    }
}
