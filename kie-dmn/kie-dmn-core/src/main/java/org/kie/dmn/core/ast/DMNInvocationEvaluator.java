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

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.core.impl.DMNContextImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.FEELImpl;
import org.kie.dmn.feel.lang.impl.NamedParameter;
import org.kie.dmn.feel.model.v1_1.Invocation;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DMNInvocationEvaluator
        implements DMNExpressionEvaluator, FEELEventListener {
    private static final Logger logger = LoggerFactory.getLogger( DMNInvocationEvaluator.class );

    private final Invocation invocation;
    private final String     nodeName;
    private final String     nodeId;
    private final String     functionName;
    private final List<ActualParameter> parameters = new ArrayList<>();
    private final FEELImpl feel;
    private final List<FEELEvent> events = new ArrayList<>();

    public DMNInvocationEvaluator(String nodeName, String nodeId, String functionName, Invocation invocation) {
        this.nodeName = nodeName;
        this.nodeId = nodeId;
        this.functionName = functionName;
        this.invocation = invocation;
        feel = (FEELImpl) FEEL.newInstance();
        feel.addListener( this );
    }

    public void addParameter(String name, DMNType type, DMNExpressionEvaluator evaluator) {
        this.parameters.add( new ActualParameter( name, type, evaluator ) );
    }

    public List<ActualParameter> getParameters() {
        return this.parameters;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        DMNResultImpl result = (DMNResultImpl) dmnr;
        DMNContext previousContext = result.getContext();
        DMNContextImpl dmnContext = (DMNContextImpl) previousContext.clone();
        result.setContext( dmnContext );
        Object invocationResult = null;

        try {
            FEELFunction function = (FEELFunction) previousContext.get( functionName );
            if ( function == null ) {
                String message = "Function '" + functionName + "' not found. Invocation failed on node '" + nodeName + "'";
                logger.error( message );
                result.addMessage(
                        DMNMessage.Severity.ERROR,
                        message,
                        nodeId );
                return new EvaluatorResultImpl( null, ResultType.FAILURE );
            }
            Object[] namedParams = new Object[parameters.size()];
            int index = 0;
            for ( ActualParameter param : parameters ) {
                try {
                    EvaluatorResult value = param.expression.evaluate( eventManager, result );
                    if ( value.getResultType() == ResultType.SUCCESS ) {
                        namedParams[index++] = new NamedParameter( param.name, value.getResult() );
                    } else {
                        String message = "Error evaluating parameter '" + param.name + "' for invocation '" + functionName + "' on node '" + nodeName + "'";
                        logger.error( message );
                        result.addMessage(
                                DMNMessage.Severity.ERROR,
                                message,
                                nodeId );
                        return new EvaluatorResultImpl( null, ResultType.FAILURE );
                    }
                } catch ( Exception e ) {
                    String message = "Error invoking parameter expression for parameter '" + param.name + "' on node '" + nodeName + "'.";
                    logger.error( message, e );
                    result.addMessage(
                            DMNMessage.Severity.ERROR,
                            message,
                            nodeId,
                            e );
                    return new EvaluatorResultImpl( null, ResultType.FAILURE );
                }
            }

            EvaluationContextImpl ctx = new EvaluationContextImpl( feel.getEventsManager() );
            invocationResult = function.invokeReflectively( ctx, namedParams );

            boolean hasErrors = hasErrors( events, eventManager, result );
            return new EvaluatorResultImpl( invocationResult, hasErrors ? ResultType.FAILURE : ResultType.SUCCESS );
        } catch ( Throwable t ) {
            String message = "Error invoking function '" + functionName + "' on node '" + nodeName + "'";
            logger.error( message );
            result.addMessage(
                    DMNMessage.Severity.ERROR,
                    message,
                    nodeId,
                    t );
        } finally {
            result.setContext( previousContext );
        }
        return new EvaluatorResultImpl( invocationResult, ResultType.SUCCESS );
    }

    private static class ActualParameter {
        final String                 name;
        final DMNType                type;
        final DMNExpressionEvaluator expression;

        public ActualParameter(String name, DMNType type, DMNExpressionEvaluator evaluator) {
            this.name = name;
            this.type = type;
            this.expression = evaluator;
        }
    }

    @Override
    public void onEvent(FEELEvent event) {
        this.events.add( event );
    }

    private boolean hasErrors(List<FEELEvent> events, DMNRuntimeEventManager eventManager, DMNResultImpl result) {
        boolean hasErrors = false;
        for ( FEELEvent e : events ) {
            if ( e.getSeverity() == FEELEvent.Severity.ERROR ) {
                result.addMessage( DMNMessage.Severity.ERROR, e.getMessage(), invocation.getId(), e );
                hasErrors = true;
            }
        }
        events.clear();
        return hasErrors;
    }

}
