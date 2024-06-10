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
package org.kie.dmn.core.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import javax.xml.namespace.QName;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.lang.impl.FEELImpl;
import org.kie.dmn.feel.lang.impl.NamedParameter;
import org.kie.dmn.feel.lang.impl.RootExecutionFrame;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.model.api.DMNElement;
import org.kie.dmn.model.api.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNInvocationEvaluator
        implements DMNExpressionEvaluator {
    private static final Logger logger = LoggerFactory.getLogger( DMNInvocationEvaluator.class );

    private final Invocation invocation;
    private final String     nodeName;
    private final DMNElement node;
    private final String     functionName;
    private final List<ActualParameter> parameters = new ArrayList<>();
    private final BiFunction<DMNContext, String, FEELFunction> functionLocator;
    private final FEEL feel;

    /**
     * @param functionLocator function to be used to resolve the FEELFunction to be invoked.
     * @param feel in case functionLocator is not able to resolve the desired function, it will be used for checking the resolution against the configured/built-in FEEL functions.
     */
    public DMNInvocationEvaluator(String nodeName, DMNElement node, String functionName, Invocation invocation, BiFunction<DMNContext, String, FEELFunction> functionLocator, FEEL feel) {
        this.nodeName = nodeName;
        this.node = node;
        this.functionName = functionName;
        this.invocation = invocation;
        if( functionLocator == null ) {
            this.functionLocator = (ctx, fname) -> (FEELFunction) ctx.get( fname );
        } else {
            this.functionLocator = functionLocator;
        }
        this.feel = feel;
    }

    public void addParameter(String name, DMNType type, DMNExpressionEvaluator evaluator) {
        this.parameters.add( new ActualParameter( name, type, evaluator ) );
    }

    public List<ActualParameter> getParameters() {
        return this.parameters;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        final List<FEELEvent> events = new ArrayList<>();
        DMNResultImpl result = (DMNResultImpl) dmnr;
        DMNContext previousContext = result.getContext();
        DMNContext dmnContext = previousContext.clone();
        result.setContext( dmnContext );
        Object invocationResult;

        try {
            boolean walkedIntoScope = false;
            StringBuilder functionNamePrefix = new StringBuilder();
            String[] fnameParts = functionName.split("\\.");
            boolean thereAreImports = !((DMNModelImpl) result.getModel()).getImportAliasesForNS().isEmpty();
            if (fnameParts.length > 1 && thereAreImports) {

                for(String part : fnameParts) {
                    functionNamePrefix.append(part);
                    QName importAlias = ((DMNModelImpl) result.getModel()).getImportAliasesForNS().get(functionNamePrefix.toString());
                    if (importAlias != null) {
                        dmnContext.pushScope(functionNamePrefix.toString(), importAlias.getNamespaceURI());
                        walkedIntoScope = true;
                        break;
                    } else {
                        functionNamePrefix.append(".");
                    }
                }
            }
            // prefix and name is separated by '.'
            functionNamePrefix.append(".");
            final String functionNameWithoutPrefix = functionName.replaceFirst(functionNamePrefix.toString(), "");
            FEELFunction function = this.functionLocator.apply(dmnContext, walkedIntoScope ? functionNameWithoutPrefix : functionName);
            if( function == null ) {
                // check if it is a configured/built-in function
                Object r;
                if (feel != null) {
                    r = ((FEELImpl) feel).newEvaluationContext(Collections.emptyList(), Collections.emptyMap()).getValue(functionName);
                } else {
                    r = RootExecutionFrame.INSTANCE.getValue( functionName );
                }
                if(r instanceof FEELFunction) {
                    function = (FEELFunction) r;
                }
            }
            // this invocation will need to resolve parameters according to the importING scope, but thanks to closure no longer need to pop scope to push it back later at actual invocation.
            if (walkedIntoScope) {
                dmnContext.popScope();
            }

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


            FEELEventListenersManager listenerMgr = new FEELEventListenersManager();
            listenerMgr.addListener(events::add);

            EvaluationContextImpl ctx = new EvaluationContextImpl(listenerMgr, eventManager.getRuntime(), FEELDialect.FEEL);

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
