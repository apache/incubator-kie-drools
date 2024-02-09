/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.util;

import java.util.Map;

import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.integration.VariableResolverFactory;

public class RawMVELEvaluator implements MVELEvaluator {

    @Override
    public Object eval(final String expression) {
        return MVEL.eval(expression);
    }

    @Override
    public Object eval(final String expression, final Object ctx) {
        return MVEL.eval(expression, ctx);
    }

    @Override
    public Object eval(final String expression, final VariableResolverFactory resolverFactory) {
        return MVEL.eval(expression, resolverFactory);
    }

    @Override
    public Object eval(final String expression, final Object ctx, final VariableResolverFactory resolverFactory) {
        return MVEL.eval(expression, ctx, resolverFactory);
    }

    @Override
    public Object eval(final String expression, final Map<String, Object> vars) {
        return MVEL.eval(expression, vars);
    }

    @Override
    public Object eval(final String expression, final Object ctx, final Map<String, Object> vars) {
        return MVEL.eval(expression, ctx, vars);
    }

    @Override
    public <T> T eval(final String expression, final Class<T> toType) {
        return MVEL.eval(expression, toType);
    }

    @Override
    public <T> T eval(final String expression, final Object ctx, final Class<T> toType) {
        return MVEL.eval(expression, ctx, toType);
    }

    @Override
    public <T> T eval(final String expression, final VariableResolverFactory vars, final Class<T> toType) {
        return MVEL.eval(expression, vars, toType);
    }

    @Override
    public <T> T eval(final String expression, final Map<String, Object> vars, final Class<T> toType) {
        return MVEL.eval(expression, vars, toType);
    }

    @Override
    public <T> T eval(final String expression, final Object ctx, final VariableResolverFactory vars, final Class<T> toType) {
        return MVEL.eval(expression, ctx, vars, toType);
    }

    @Override
    public <T> T eval(final String expression, final Object ctx, final Map<String, Object> vars, final Class<T> toType) {
        return MVEL.eval(expression, ctx, vars, toType);
    }

    @Override
    public String evalToString(final String expression) {
         return MVEL.evalToString(expression);
    }

    @Override
    public Object executeExpression(final Object compiledExpression) {
        return MVEL.executeExpression(compiledExpression);
    }

    @Override
    public Object executeExpression(final Object compiledExpression, final Object ctx, final Map vars) {
        return MVEL.executeExpression(compiledExpression, ctx, vars);
    }

    @Override
    public Object executeExpression(final Object compiledExpression, final Object ctx, final VariableResolverFactory resolverFactory) {
        return MVEL.executeExpression(compiledExpression, ctx, resolverFactory);
    }

    @Override
    public Object executeExpression(final Object compiledExpression, final VariableResolverFactory factory) {
        return MVEL.executeExpression(compiledExpression, factory);
    }

    @Override
    public Object executeExpression(final Object compiledExpression, final Object ctx) {
        return MVEL.executeExpression(compiledExpression, ctx);
    }

    @Override
    public Object executeExpression(final Object compiledExpression, final Map vars) {
        return MVEL.executeExpression(compiledExpression, vars);
    }

    @Override
    public <T> T executeExpression(final Object compiledExpression, final Object ctx, final Map vars, final Class<T> toType) {
        return MVEL.executeExpression(compiledExpression, ctx, vars, toType);
    }

    @Override
    public <T> T executeExpression(final Object compiledExpression, final Object ctx, final VariableResolverFactory vars, final Class<T> toType) {
        return MVEL.executeExpression(compiledExpression, ctx, vars, toType);
    }

    @Override
    public <T> T executeExpression(final Object compiledExpression, final Map vars, final Class<T> toType) {
        return MVEL.executeExpression(compiledExpression, vars, toType);
    }

    @Override
    public <T> T executeExpression(final Object compiledExpression, final Object ctx, final Class<T> toType) {
        return MVEL.executeExpression(compiledExpression, ctx, toType);
    }

    @Override
    public void executeExpression(final Iterable<CompiledExpression> compiledExpression) {
        MVEL.executeExpression(compiledExpression);
    }

    @Override
    public void executeExpression(final Iterable<CompiledExpression> compiledExpression, final Object ctx) {
        MVEL.executeExpression(compiledExpression, ctx);
    }

    @Override
    public void executeExpression(final Iterable<CompiledExpression> compiledExpression, final Map vars) {
        MVEL.executeExpression(compiledExpression, vars);
    }

    @Override
    public void executeExpression(final Iterable<CompiledExpression> compiledExpression, final Object ctx, final Map vars) {
        MVEL.executeExpression(compiledExpression, ctx, vars);
    }

    @Override
    public void executeExpression(final Iterable<CompiledExpression> compiledExpression, final Object ctx, final VariableResolverFactory vars) {
        MVEL.executeExpression(compiledExpression, ctx, vars);
    }

}
