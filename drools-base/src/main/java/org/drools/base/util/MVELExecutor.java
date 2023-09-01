package org.drools.base.util;

import java.util.Map;

public interface MVELExecutor {
    Object eval(String expression);

    Object eval(String expression, Object ctx);

    Object eval(String expression, Map<String, Object> vars);

    Object eval(String expression, Object ctx, Map<String, Object> vars);

    <T> T eval(String expression, Class<T> toType);

    <T> T eval(String expression, Object ctx, Class<T> toType);

    <T> T eval(String expression, Map<String, Object> vars, Class<T> toType);

    <T> T eval(String expression, Object ctx, Map<String, Object> vars, Class<T> toType);

    String evalToString(String singleValue);

    Object executeExpression(Object compiledExpression);

    Object executeExpression(final Object compiledExpression, final Object ctx, final Map vars);

    Object executeExpression(final Object compiledExpression, final Object ctx);

    Object executeExpression(final Object compiledExpression, final Map vars);

    <T> T executeExpression(final Object compiledExpression, final Object ctx, final Map vars, Class<T> toType);

    <T> T executeExpression(final Object compiledExpression, Map vars, Class<T> toType);

    <T> T executeExpression(final Object compiledExpression, final Object ctx, Class<T> toType);

    String soundex(String s);
}
