/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.lang.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.DashNode;
import org.kie.dmn.feel.lang.ast.ListNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.dmn.feel.runtime.UnaryTest;

/**
 * Language runtime entry point
 */
public class FEELImpl
        implements FEEL {

    private static final Map<String,Object> EMPTY_INPUT = Collections.emptyMap();

    private Set<FEELEventListener> instanceEventListeners = new HashSet<>();

    @Override
    public CompilerContext newCompilerContext() {
        return newCompilerContext(Collections.emptySet());
    }
    
    public CompilerContext newCompilerContext(Collection<FEELEventListener> contextListeners) {
        return new CompilerContextImpl( getEventsManager(contextListeners) );
    }
    
    @Override
    public CompiledExpression compile(String expression, CompilerContext ctx) {
        FEEL_1_1Parser parser = FEELParser.parse( getEventsManager(ctx.getListeners()), expression, ctx.getInputVariableTypes(), ctx.getInputVariables() );
        ParseTree tree = parser.compilation_unit();
        ASTBuilderVisitor v = new ASTBuilderVisitor( ctx.getInputVariableTypes() );
        BaseNode expr = v.visit( tree );
        CompiledExpression ce = new CompiledExpressionImpl( expr );
        return ce;
    }

    public CompiledExpression compileExpressionList(String expression, CompilerContext ctx) {
        FEEL_1_1Parser parser = FEELParser.parse( getEventsManager(ctx.getListeners()), expression, ctx.getInputVariableTypes(), ctx.getInputVariables() );
        ParseTree tree = parser.expressionList();
        ASTBuilderVisitor v = new ASTBuilderVisitor( ctx.getInputVariableTypes() );
        BaseNode expr = v.visit( tree );
        CompiledExpression ce = new CompiledExpressionImpl( expr );
        return ce;
    }

    @Override
    public Object evaluate(String expression) {
        return evaluate( expression, FEELImpl.EMPTY_INPUT );
    }
    
    @Override
    public Object evaluate(String expression, EvaluationContext ctx) {
        CompilerContext compilerCtx = newCompilerContext(ctx.getListeners());
        Map<String, Object> inputVariables = ctx.getAllValues();
        if ( inputVariables != null ) {
            inputVariables.entrySet().stream().forEach( e -> compilerCtx.addInputVariable( e.getKey(), e.getValue() ) );
        }
        CompiledExpression expr = compile( expression, compilerCtx );
        return evaluate( expr, ctx );
    }

    @Override
    public Object evaluate(String expression, Map<String, Object> inputVariables) {
        CompilerContext ctx = newCompilerContext();
        if ( inputVariables != null ) {
            inputVariables.entrySet().stream().forEach( e -> ctx.addInputVariable( e.getKey(), e.getValue() ) );
        }
        CompiledExpression expr = compile( expression, ctx );
        if ( inputVariables == null ) {
            return evaluate( expr, EMPTY_INPUT );
        } else {
            return evaluate( expr, inputVariables );
        }
    }

    @Override
    public Object evaluate(CompiledExpression expr, Map<String, Object> inputVariables) {
        return ((CompiledExpressionImpl) expr).evaluate( getEventsManager(Collections.emptySet()), inputVariables );
    }
    
    @Override
    public Object evaluate(CompiledExpression expr, EvaluationContext ctx) {
        return ((CompiledExpressionImpl) expr).evaluate( getEventsManager(ctx.getListeners()), ctx.getAllValues() );
    }

    @Override
    public List<UnaryTest> evaluateUnaryTests(String expression) {
        return evaluateUnaryTests( expression, Collections.emptyMap() );
    }

    @Override
    public List<UnaryTest> evaluateUnaryTests(String expression, Map<String, Type> variableTypes) {
        // DMN defines a special case where, unless the expressions are unary tests
        // or ranges, they need to be converted into an equality test unary expression.
        // This way, we have to compile and check the low level AST nodes to properly
        // deal with this case
        CompilerContext ctx = newCompilerContext();
        for( Map.Entry<String, Type> e : variableTypes.entrySet() ) {
            ctx.addInputVariableType( e.getKey(), e.getValue() );
        }
        CompiledExpressionImpl compiledExpression = (CompiledExpressionImpl) compileExpressionList( expression, ctx );
        if( compiledExpression != null ) {
            ListNode listNode = (ListNode) compiledExpression.getExpression();
            List<BaseNode> tests = new ArrayList<>(  );
            for( BaseNode o : listNode.getElements() ) {
                if ( o == null ) {
                    // not much we can do, so just skip it. Error was reported somewhere else
                    continue;
                } else if ( o instanceof UnaryTestNode || o instanceof DashNode ) {
                    tests.add( o );
                } else if( o instanceof RangeNode ) {
                    tests.add( new UnaryTestNode( "in", o ) );
                } else {
                    tests.add( new UnaryTestNode( "=", o ) );
                }
            }
            listNode.setElements( tests );
            compiledExpression.setExpression( listNode );

            // now we can evaluate the expression to build the list of unary tests
            List<UnaryTest> uts = (List<UnaryTest>) evaluate( compiledExpression, FEELImpl.EMPTY_INPUT );
            return uts;
        }
        return Collections.emptyList();
    }

    @Override
    public void addListener(FEELEventListener listener) {
        instanceEventListeners.add( listener );
    }

    @Override
    public void removeListener(FEELEventListener listener) {
        instanceEventListeners.remove( listener );
    }

    @Override
    public Set<FEELEventListener> getListeners() {
        return Collections.unmodifiableSet(instanceEventListeners);
    }

    public FEELEventListenersManager getEventsManager(Collection<FEELEventListener> contextListeners) {
        FEELEventListenersManager listenerMgr = new FEELEventListenersManager();
        listenerMgr.addListeners(instanceEventListeners);
        listenerMgr.addListeners(contextListeners);
        return listenerMgr;
    }

}
