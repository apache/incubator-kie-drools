package org.drools.model.codegen.execmodel.generator.expressiontyper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.expr.Expression;
import org.drools.model.codegen.execmodel.generator.TypedExpression;

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

    public TypedExpression typedExpressionOrException() {
        return typedExpression.orElseThrow(() -> new CannotTypeExpressionException(
                String.format("Cannot type expression: %s", expressionTyperContext.getOriginalExpression())));
    }

    public Set<String> getUsedDeclarations() {
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

    public List<Expression> getNullSafeExpressions() {
        return expressionTyperContext.getNullSafeExpressions();
    }

    public Optional<Expression> getInlineCastExpression() {
        return expressionTyperContext.getInlineCastExpression();
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
