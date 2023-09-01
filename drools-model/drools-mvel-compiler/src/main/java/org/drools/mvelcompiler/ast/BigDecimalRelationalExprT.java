package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;

import static com.github.javaparser.ast.NodeList.nodeList;

public class BigDecimalRelationalExprT implements TypedExpression {

    private final BinaryExpr.Operator operator;
    private final TypedExpression argument;
    private final TypedExpression scope;
    private final Type type = BigDecimal.class;

    public BigDecimalRelationalExprT(BinaryExpr.Operator operator,
                                     TypedExpression scope,
                                     TypedExpression argument) {
        this.operator = operator;
        this.scope = scope;
        this.argument = argument;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    @Override
    public Node toJavaExpression() {
        MethodCallExpr methodCallExpr = new MethodCallExpr((Expression) scope.toJavaExpression(), "compareTo",
                                                           nodeList((Expression) argument.toJavaExpression()));
        return new BinaryExpr(methodCallExpr, new IntegerLiteralExpr("0"), operator);
    }

    @Override
    public String toString() {
        return "BigDecimalRelationalExprT{" +
                "operator=" + operator +
                ", argument=" + argument +
                ", scope=" + scope +
                ", type=" + type +
                '}';
    }
}
