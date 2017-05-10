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

import org.drools.core.rule.Function;
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
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.FEELImpl;
import org.kie.dmn.feel.lang.impl.NamedParameter;
import org.kie.dmn.model.v1_1.DMNElement;
import org.kie.dmn.model.v1_1.Invocation;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class DMNInvocationEvaluator
        implements DMNExpressionEvaluator, FEELEventListener {
    private static final Logger logger = LoggerFactory.getLogger( DMNInvocationEvaluator.class );

    private final Invocation invocation;
    private final String     nodeName;
    private final DMNElement node;
    private final String     functionName;
    private final List<ActualParameter> parameters = new ArrayList<>();
    private final FEELImpl feel;
    private final List<FEELEvent> events = new ArrayList<>();
    private final BiFunction<DMNContext, String, FEELFunction> functionLocator;

    public DMNInvocationEvaluator(String nodeName, DMNElement node, String functionName, Invocation invocation, BiFunction<DMNContext, String, FEELFunction> functionLocator ) {
        this.nodeName = nodeName;
        this.node = node;
        this.functionName = functionName;
        this.invocation = invocation;
        if( functionLocator == null ) {
            this.functionLocator = (ctx, fname) -> (FEELFunction) ctx.get( fname );
        } else {
            this.functionLocator = functionLocator;
        }
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
            FEELFunction function = this.functionLocator.apply( previousContext, functionName );
            if ( function == null ) {
                MsgUtil.reportMessage( logger,
                                       DMNMessage.Severity.ERROR,
                                       node,
                                       result,
                                       null,
                                       null,
                                       Msg.FUNCTION_NOT_FOUND,
                                       functionName,
                                       nodeName );
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
                        MsgUtil.reportMessage( logger,
                                               DMNMessage.Severity.ERROR,
                                               node,
                                               result,
                                               null,
                                               null,
                                               Msg.ERR_EVAL_PARAM_FOR_INVOCATION_ON_NODE,
                                               param.name,
                                               functionName,
                                               nodeName );
                        return new EvaluatorResultImpl( null, ResultType.FAILURE );
                    }
                } catch ( Exception e ) {
                    MsgUtil.reportMessage( logger,
                                           DMNMessage.Severity.ERROR,
                                           node,
                                           result,
                                           e,
                                           null,
                                           Msg.ERR_INVOKING_PARAM_EXPR_FOR_PARAM_ON_NODE,
                                           param.name,
                                           nodeName );
                    return new EvaluatorResultImpl( null, ResultType.FAILURE );
                }
            }

            EvaluationContextImpl ctx = new EvaluationContextImpl( feel.getEventsManager() );
            invocationResult = function.invokeReflectively( ctx, namedParams );

            boolean hasErrors = hasErrors( events, eventManager, result );
            return new EvaluatorResultImpl( invocationResult, hasErrors ? ResultType.FAILURE : ResultType.SUCCESS );
        } catch ( Throwable t ) {
            MsgUtil.reportMessage( logger,
                                   DMNMessage.Severity.ERROR,
                                   node,
                                   result,
                                   t,
                                   null,
                                   Msg.ERR_INVOKING_FUNCTION_ON_NODE,
                                   functionName,
                                   nodeName );
            return new EvaluatorResultImpl( null, ResultType.FAILURE );
        } finally {
            result.setContext( previousContext );
        }
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
                MsgUtil.reportMessage( logger,
                                       DMNMessage.Severity.ERROR,
                                       invocation,
                                       result,
                                       null,
                                       e,
                                       Msg.FEEL_ERROR,
                                       e.getMessage() );
                hasErrors = true;
            }
        }
        events.clear();
        return hasErrors;
    }

}
