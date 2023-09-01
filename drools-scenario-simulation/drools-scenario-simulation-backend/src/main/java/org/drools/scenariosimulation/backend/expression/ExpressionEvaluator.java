package org.drools.scenariosimulation.backend.expression;

import java.util.List;

public interface ExpressionEvaluator {

    ExpressionEvaluatorResult evaluateUnaryExpression(String rawExpression, Object resultValue, Class<?> resultClass);

    Object evaluateLiteralExpression(String rawExpression, String className, List<String> genericClasses);

    String fromObjectToExpression(Object value);
}
