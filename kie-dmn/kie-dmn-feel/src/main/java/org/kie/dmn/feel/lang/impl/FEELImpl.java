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

import org.antlr.v4.runtime.tree.ParseTree;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.*;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.dmn.feel.runtime.UnaryTest;

import java.util.*;

/**
 * Language runtime entry point
 */
public class FEELImpl
        implements FEEL {

    private static final Map<String,Object> EMPTY_INPUT = Collections.emptyMap();

    private FEELEventListenersManager eventsManager = new FEELEventListenersManager();

    public CompilerContext newCompilerContext() {
        return new CompilerContextImpl( eventsManager );
    }

    public CompiledExpression compile(String expression, CompilerContext ctx) {
        FEEL_1_1Parser parser = FEELParser.parse( eventsManager, expression, ctx.getInputVariableTypes(), ctx.getInputVariables() );
        ParseTree tree = parser.compilation_unit();
        ASTBuilderVisitor v = new ASTBuilderVisitor();
        BaseNode expr = v.visit( tree );
        CompiledExpression ce = new CompiledExpressionImpl( expr );
        return ce;
    }

    public CompiledExpression compileExpressionList(String expression, CompilerContext ctx) {
        FEEL_1_1Parser parser = FEELParser.parse( eventsManager, expression, ctx.getInputVariableTypes(), ctx.getInputVariables() );
        ParseTree tree = parser.expressionList();
        ASTBuilderVisitor v = new ASTBuilderVisitor();
        BaseNode expr = v.visit( tree );
        CompiledExpression ce = new CompiledExpressionImpl( expr );
        return ce;
    }

    public Object evaluate(String expression) {
        return evaluate( expression, FEELImpl.EMPTY_INPUT );
    }

    public Object evaluate(String expression, Map<String, Object> inputVariables) {
        CompilerContext ctx = newCompilerContext();
        if ( inputVariables != null ) {
            inputVariables.entrySet().stream().forEach( e -> ctx.addInputVariable( e.getKey(), e.getValue() ) );
        }
        CompiledExpression expr = compile( expression, ctx );
        return evaluate( expr, inputVariables );
    }

    public Object evaluate(CompiledExpression expr, Map<String, Object> inputVariables) {
        return ((CompiledExpressionImpl) expr).evaluate( eventsManager, inputVariables );
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
        this.eventsManager.addListener( listener );
    }

    @Override
    public void removeListener(FEELEventListener listener) {
        this.eventsManager.removeListener( listener );
    }

    @Override
    public Set<FEELEventListener> getListeners() {
        return eventsManager.getListeners();
    }

    public FEELEventListenersManager getEventsManager() {
        return this.eventsManager;
    }

}
