package org.drools.mvel.util;

import java.util.Map;

import org.mvel2.compiler.CompiledExpression;
import org.mvel2.integration.VariableResolverFactory;

public interface MVELEvaluator {

    Object eval(String expression);

    Object eval(String expression, Object ctx);

    Object eval(String expression, VariableResolverFactory resolverFactory);

    Object eval(String expression, Object ctx, VariableResolverFactory resolverFactory);

    Object eval(String expression, Map<String, Object> vars);

    Object eval(String expression, Object ctx, Map<String, Object> vars);

    <T> T eval(String expression, Class<T> toType);

    <T> T eval(String expression, Object ctx, Class<T> toType);

    <T> T eval(String expression, VariableResolverFactory vars, Class<T> toType);

    <T> T eval(String expression, Map<String, Object> vars, Class<T> toType);

    <T> T eval(String expression, Object ctx, VariableResolverFactory vars, Class<T> toType);

    <T> T eval(String expression, Object ctx, Map<String, Object> vars, Class<T> toType);

    String evalToString(String singleValue);

    Object executeExpression(Object compiledExpression);

    Object executeExpression(final Object compiledExpression, final Object ctx, final Map vars);

    Object executeExpression(final Object compiledExpression, final Object ctx, final VariableResolverFactory resolverFactory);

    Object executeExpression(final Object compiledExpression, final VariableResolverFactory factory);

    Object executeExpression(final Object compiledExpression, final Object ctx);

    Object executeExpression(final Object compiledExpression, final Map vars);

    <T> T executeExpression(final Object compiledExpression, final Object ctx, final Map vars, Class<T> toType);

    <T> T executeExpression(final Object compiledExpression, final Object ctx, final VariableResolverFactory vars, Class<T> toType);

    <T> T executeExpression(final Object compiledExpression, Map vars, Class<T> toType);

    <T> T executeExpression(final Object compiledExpression, final Object ctx, Class<T> toType);

    void executeExpression(Iterable<CompiledExpression> compiledExpression);

    void executeExpression(Iterable<CompiledExpression> compiledExpression, Object ctx);

    void executeExpression(Iterable<CompiledExpression> compiledExpression, Map vars);

    void executeExpression(Iterable<CompiledExpression> compiledExpression, Object ctx, Map vars);

    void executeExpression(Iterable<CompiledExpression> compiledExpression, Object ctx, VariableResolverFactory vars);

}
