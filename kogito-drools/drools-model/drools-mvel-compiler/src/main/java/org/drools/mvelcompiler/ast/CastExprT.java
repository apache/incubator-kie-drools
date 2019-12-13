package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;

public class CastExprT implements TypedExpression {

    private final TypedExpression innerExpr;
    private final Class<?> type;

    public CastExprT(TypedExpression innerExpr, Class<?> type) {
        this.innerExpr = innerExpr;
        this.type = type;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.of(type);
    }

    @Override
    public Node toJavaExpression() {
        com.github.javaparser.ast.type.Type jpType = StaticJavaParser.parseType(this.type.getCanonicalName());
        Expression expression = (Expression) innerExpr.toJavaExpression();
        return new EnclosedExpr(new CastExpr(jpType, expression));
    }
}
