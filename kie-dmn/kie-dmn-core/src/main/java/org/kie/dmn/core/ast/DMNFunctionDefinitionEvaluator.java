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

package org.kie.dmn.core.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.impl.DMNContextFEELCtxWrapper;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.model.v1_1.FunctionDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNFunctionDefinitionEvaluator
        implements DMNExpressionEvaluator {
    private static final Logger logger = LoggerFactory.getLogger( DMNFunctionDefinitionEvaluator.class );

    private final String name;
    private final FunctionDefinition functionDefinition;
    private List<FormalParameter> parameters = new ArrayList<>(  );
    private DMNExpressionEvaluator evaluator;

    public DMNFunctionDefinitionEvaluator(String name, FunctionDefinition fdef ) {
        this.name = name;
        this.functionDefinition = fdef;
    }

    public DMNType getParameterType( String name ) {
        for( FormalParameter fp : parameters ) {
            if( fp.name.equals( name ) ) {
                return fp.type;
            }
        }
        return null;
    }

    public List<List<String>> getParameterNames() {
        return Collections.singletonList( parameters.stream().map( p -> p.name ).collect( Collectors.toList()) );
    }

    public List<List<DMNType>> getParameterTypes() {
        return Collections.singletonList( parameters.stream().map( p -> p.type ).collect( Collectors.toList()) );
    }

    public void addParameter(String name, DMNType dmnType) {
        this.parameters.add( new FormalParameter( name, dmnType ) );
    }

    public void setEvaluator(DMNExpressionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public DMNExpressionEvaluator getEvaluator() {
        return this.evaluator;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        DMNResultImpl result = (DMNResultImpl) dmnr;
        // when this evaluator is executed, it should return a "FEEL function" to register in the context
        DMNFunction function = new DMNFunction( name, parameters, functionDefinition, evaluator, eventManager, result );
        return new EvaluatorResultImpl( function, ResultType.SUCCESS );
    }

    private static class FormalParameter {
        final String name;
        final DMNType type;

        public FormalParameter(String name, DMNType type) {
            this.name = name;
            this.type = type;
        }
    }

    public static class DMNFunction
            extends BaseFEELFunction {
        private final List<FormalParameter> parameters;
        private final DMNExpressionEvaluator evaluator;
        private final DMNRuntimeEventManager eventManager;
        private final DMNResultImpl resultContext;
        private final FunctionDefinition functionDefinition;

        public DMNFunction(String name, List<FormalParameter> parameters, FunctionDefinition functionDefinition, DMNExpressionEvaluator evaluator, DMNRuntimeEventManager eventManager, DMNResultImpl result) {
            super( name );
            this.functionDefinition = functionDefinition;
            this.parameters = parameters;
            this.evaluator = evaluator;
            this.eventManager = eventManager;
            this.resultContext = result;
        }

        public Object invoke(EvaluationContext ctx, Object[] params) {
            DMNContext previousContext = resultContext.getContext();
            // we could be more strict and only set the parameters and the dependencies as values in the new
            // context, but for now, cloning the original context
            DMNContextFEELCtxWrapper dmnContext = new DMNContextFEELCtxWrapper(ctx);
            dmnContext.enterFrame();
            try {
                if( evaluator != null ) {
                    previousContext.getAll().forEach(dmnContext::set);
                    for( int i = 0; i < params.length; i++ ) {
                        dmnContext.set( parameters.get( i ).name, params[i] );
                    }
                    resultContext.setContext( dmnContext );
                    EvaluatorResult result = evaluator.evaluate( eventManager, resultContext );
                    if( result.getResultType() == ResultType.SUCCESS ) {
                        return result.getResult();
                    }
                    return null;
                } else {
                    MsgUtil.reportMessage( logger,
                                           DMNMessage.Severity.ERROR,
                                           functionDefinition,
                                           resultContext,
                                           null,
                                           null,
                                           Msg.MISSING_EXPRESSION_FOR_FUNCTION,
                                           getName() );
                    return null;
                }
            } catch ( Exception e ) {
                MsgUtil.reportMessage( logger,
                                       DMNMessage.Severity.ERROR,
                                       functionDefinition,
                                       resultContext,
                                       e,
                                       null,
                                       Msg.ERR_INVOKING_FUNCTION_ON_NODE,
                                       getName(),
                                       getName() );
                return null;
            } finally {
                resultContext.setContext( previousContext );
                dmnContext.exitFrame();
            }
        }

        @Override
        protected boolean isCustomFunction() {
            return true;
        }

        public List<List<String>> getParameterNames() {
            return Collections.singletonList( parameters.stream().map( p -> p.name ).collect( Collectors.toList()) );
        }

        public List<List<DMNType>> getParameterTypes() {
            return Collections.singletonList( parameters.stream().map( p -> p.type ).collect( Collectors.toList()) );
        }

        public String toString() {
            return "function "+getName()+"( "+parameters.stream().map( p -> p.name ).collect( Collectors.joining( ", " ) )+" )";
        }
    }

}
