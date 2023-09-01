package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.UnaryExpr;

import static com.github.javaparser.ast.NodeList.nodeList;

public class BigDecimalConvertedExprT implements TypedExpression {

    private final TypedExpression value;
    private final Type type = BigDecimal.class;

    private final Optional<UnaryExpr> unaryExpr;

    public BigDecimalConvertedExprT(TypedExpression value) {
        this(value, Optional.empty());
    }

    public BigDecimalConvertedExprT(TypedExpression value, Optional<UnaryExpr> unaryExpr) {
        this.value = value;
        this.unaryExpr = unaryExpr;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    @Override
    public Node toJavaExpression() {
        Expression expr = (Expression) value.toJavaExpression();
        Expression arg = unaryExpr.map(u -> (Expression) new UnaryExpr(expr, u.getOperator())).orElse(expr);
        return new ObjectCreationExpr(null, StaticJavaParser.parseClassOrInterfaceType(type.getTypeName()), nodeList(arg));
    }

    @Override
    public String toString() {
        return "BigDecimalConstantExprT{" +
                "value=" + value +
                '}';
    }
}
