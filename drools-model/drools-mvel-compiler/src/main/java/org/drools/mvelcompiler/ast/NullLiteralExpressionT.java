package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import org.drools.core.util.MethodUtils.NullType;

public class NullLiteralExpressionT implements TypedExpression {

    private final NullLiteralExpr nullLiteralExpr;

    public NullLiteralExpressionT( NullLiteralExpr nullLiteralExpr) {
        this.nullLiteralExpr = nullLiteralExpr;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(NullType.class);
    }

    @Override
    public Node toJavaExpression() {
        return nullLiteralExpr;
    }

    @Override
    public String toString() {
        return "NullLiteralExpressionT{" +
                "originalExpression=" + nullLiteralExpr +
                '}';
    }
}
