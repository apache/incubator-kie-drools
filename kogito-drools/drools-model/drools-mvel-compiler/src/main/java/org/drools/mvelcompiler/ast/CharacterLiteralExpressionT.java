package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

public class CharacterLiteralExpressionT implements TypedExpression {

    private final CharLiteralExpr charLiteralExpr;

    public CharacterLiteralExpressionT(CharLiteralExpr n) {
        this.charLiteralExpr = n;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(String.class);
    }

    @Override
    public Node toJavaExpression() {
        return new StringLiteralExpr(charLiteralExpr.getValue());
    }
}
