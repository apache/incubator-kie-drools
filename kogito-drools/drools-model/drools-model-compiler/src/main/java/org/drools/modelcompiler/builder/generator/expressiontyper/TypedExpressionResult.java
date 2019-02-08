package org.drools.modelcompiler.builder.generator.expressiontyper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.drools.javaparser.ast.expr.Expression;
import org.drools.modelcompiler.builder.generator.TypedExpression;

public class TypedExpressionResult {

    final Optional<TypedExpression> typedExpression;
    final ExpressionTyperContext expressionTyperContext;

    public TypedExpressionResult(Optional<TypedExpression> typedExpression, ExpressionTyperContext expressionTyperContext) {
        this.typedExpression = typedExpression;
        this.expressionTyperContext = expressionTyperContext;
    }

    public Optional<TypedExpression> getTypedExpression() {
        return typedExpression;
    }

    public List<String> getUsedDeclarations() {
        return expressionTyperContext.getUsedDeclarations();
    }

    public Set<String> getReactOnProperties() {
        return expressionTyperContext.getReactOnProperties();
    }

    public ExpressionTyperContext getExpressionTyperContext() {
        return expressionTyperContext;
    }

    public List<Expression> getPrefixExpressions() {
        return expressionTyperContext.getPrefixExpresssions();
    }

    @Override
    public String toString() {
        return "{" +
                "expression=" + typedExpression.map(TypedExpression::toString).orElse("Parse Fail") +
                ", usedDeclarations=" + expressionTyperContext.getUsedDeclarations() +
                ", reactOnProperties =" + expressionTyperContext.getReactOnProperties() +
                '}';
    }
}
