package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import static com.github.javaparser.ast.NodeList.nodeList;

public class BigDecimalArithmeticExprT implements TypedExpression {

    private final String name;
    private final TypedExpression argument;
    private final TypedExpression scope;
    private final Type type = BigDecimal.class;

    public static String toBigDecimalMethod(BinaryExpr.Operator operator) {
        switch (operator) {
            case PLUS: // +
                return "add";
            case MINUS: // -
                return "subtract";
            case MULTIPLY: // *
                return "multiply";
            case DIVIDE: // /
                return "divide";
            case REMAINDER: // %
                return "remainder";
        }
        throw new RuntimeException("Unknown operator");
    }

    public static String toBigDecimalMethod(AssignExpr.Operator operator) {
        switch (operator) {
            case PLUS: // +=
                return "add";
            case MINUS: // -=
                return "subtract";
            case MULTIPLY: // *=
                return "multiply";
            case DIVIDE: // /=
                return "divide";
            case ASSIGN: // =
                return "valueOf";
        }
        throw new RuntimeException("Unknown operator");
    }

    public BigDecimalArithmeticExprT(String bigDecimalMethod,
                                     TypedExpression scope,
                                     TypedExpression argument) {
        this.name = bigDecimalMethod;
        this.scope = scope;
        this.argument = argument;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    @Override
    public Node toJavaExpression() {

        return new MethodCallExpr((Expression) scope.toJavaExpression(),
                                  name,
                                  nodeList((Expression) argument.toJavaExpression()));
    }

    @Override
    public String toString() {
        return "BigDecimalExprT{" +
                "name='" + name + '\'' +
                ", argument=" + argument +
                ", type=" + type +
                '}';
    }
}
