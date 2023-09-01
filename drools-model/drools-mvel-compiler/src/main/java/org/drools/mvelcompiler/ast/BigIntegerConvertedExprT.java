package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import static com.github.javaparser.ast.NodeList.nodeList;

public class BigIntegerConvertedExprT implements TypedExpression {

    private final TypedExpression value;
    private final Type type = BigInteger.class;

    public BigIntegerConvertedExprT(TypedExpression value) {
        this.value = value;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    @Override
    public Node toJavaExpression() {

        return new ObjectCreationExpr(null,
                                      StaticJavaParser.parseClassOrInterfaceType(type.getTypeName()),
                                      nodeList((Expression) value.toJavaExpression()));
    }

    @Override
    public String toString() {
        return "BigIntegerConvertedExprT{" +
                "value=" + value +
                '}';
    }
}
