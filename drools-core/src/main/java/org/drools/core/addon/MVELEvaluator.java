/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.addon;

import java.util.Map;

import org.mvel2.compiler.CompiledExpression;
import org.mvel2.integration.VariableResolverFactory;

public interface MVELEvaluator {

    public Object eval( String expression );

    public Object eval( String expression, Object ctx );

    public Object eval( String expression, VariableResolverFactory resolverFactory );

    public Object eval( String expression, Object ctx, VariableResolverFactory resolverFactory );

    public Object eval( String expression, Map<String, Object> vars );

    public Object eval( String expression, Object ctx, Map<String, Object> vars );

    public <T> T eval( String expression, Class<T> toType );

    public <T> T eval( String expression, Object ctx, Class<T> toType );

    public <T> T eval( String expression, VariableResolverFactory vars, Class<T> toType );

    public <T> T eval( String expression, Map<String, Object> vars, Class<T> toType );

    public <T> T eval( String expression, Object ctx, VariableResolverFactory vars, Class<T> toType );

    public <T> T eval( String expression, Object ctx, Map<String, Object> vars, Class<T> toType );

    public String evalToString( String singleValue );

    public Object executeExpression( Object compiledExpression );

    public Object executeExpression( final Object compiledExpression, final Object ctx, final Map vars );

    public Object executeExpression( final Object compiledExpression, final Object ctx, final VariableResolverFactory resolverFactory );

    public Object executeExpression( final Object compiledExpression, final VariableResolverFactory factory );

    public Object executeExpression( final Object compiledExpression, final Object ctx );

    public Object executeExpression( final Object compiledExpression, final Map vars );

    public <T> T executeExpression( final Object compiledExpression, final Object ctx, final Map vars, Class<T> toType );

    public <T> T executeExpression( final Object compiledExpression, final Object ctx, final VariableResolverFactory vars, Class<T> toType );

    public <T> T executeExpression( final Object compiledExpression, Map vars, Class<T> toType );

    public <T> T executeExpression( final Object compiledExpression, final Object ctx, Class<T> toType );

    public void executeExpression( Iterable<CompiledExpression> compiledExpression );

    public void executeExpression( Iterable<CompiledExpression> compiledExpression, Object ctx );

    public void executeExpression( Iterable<CompiledExpression> compiledExpression, Map vars );

    public void executeExpression( Iterable<CompiledExpression> compiledExpression, Object ctx, Map vars );

    public void executeExpression( Iterable<CompiledExpression> compiledExpression, Object ctx, VariableResolverFactory vars );

}