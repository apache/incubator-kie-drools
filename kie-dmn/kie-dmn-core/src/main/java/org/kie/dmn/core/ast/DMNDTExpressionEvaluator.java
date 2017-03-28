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

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.impl.DMNRuntimeEventManagerImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.FEELImpl;
import org.kie.dmn.feel.runtime.events.DecisionTableRulesSelectedEvent;
import org.kie.dmn.feel.runtime.events.DecisionTableRulesMatchedEvent;
import org.kie.dmn.feel.runtime.functions.DTInvokerFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * An evaluator for DMN Decision Table Expressions
 */
public class DMNDTExpressionEvaluator
        implements DMNExpressionEvaluator, FEELEventListener {
    private static Logger logger = LoggerFactory.getLogger( DMNDTExpressionEvaluator.class );

    private final DMNNode           node;
    private       DTInvokerFunction dt;
    private       FEELImpl          feel;

    private List<FEELEvent> events = new ArrayList<>();

    public DMNDTExpressionEvaluator(DMNNode node, DTInvokerFunction dt) {
        this.node = node;
        this.dt = dt;
        feel = (FEELImpl) FEEL.newInstance();
        feel.addListener( this );
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager dmrem, DMNResult dmnr) {
        DMNRuntimeEventManagerImpl eventManager = (DMNRuntimeEventManagerImpl) dmrem;
        DMNResultImpl result = (DMNResultImpl) dmnr;
        EventResults r = null;
        try {
            eventManager.fireBeforeEvaluateDecisionTable( node.getName(), dt.getName(), result );
            List<String> paramNames = dt.getParameterNames().get( 0 );
            Object[] params = new Object[paramNames.size()];
            EvaluationContextImpl ctx = new EvaluationContextImpl( feel.getEventsManager() );

            ctx.enterFrame();
            // need to set the values for in context variables...
            for ( Map.Entry<String,Object> entry : result.getContext().getAll().entrySet() ) {
                ctx.setValue( entry.getKey(), entry.getValue() );
            }
            for ( int i = 0; i < params.length; i++ ) {
                params[i] = feel.evaluate( paramNames.get( i ), result.getContext().getAll() );
                ctx.setValue( paramNames.get( i ), params[i] );
            }
            Object dtr = dt.invoke( ctx, params ).cata( e -> { events.add( e); return null; }, Function.identity());

            // since ctx is a local variable that will be discarded, no need for a try/finally,
            // but still wanted to match the enter/exit frame for future maintainability purposes
            ctx.exitFrame();

            r = processEvents( events, eventManager, result );
            return new EvaluatorResultImpl( dtr, r.hasErrors ? ResultType.FAILURE : ResultType.SUCCESS );
        } finally {
            eventManager.fireAfterEvaluateDecisionTable( node.getName(), dt.getName(), result, (r != null ? r.matchedRules : null), (r != null ? r.fired : null) );
        }
    }

    private EventResults processEvents(List<FEELEvent> events, DMNRuntimeEventManager eventManager, DMNResultImpl result) {
        EventResults r = new EventResults();
        for ( FEELEvent e : events ) {
            if ( e instanceof DecisionTableRulesMatchedEvent ) {
                r.matchedRules = ((DecisionTableRulesMatchedEvent) e).getMatches();
            } else if ( e instanceof DecisionTableRulesSelectedEvent ) {
                r.fired = ((DecisionTableRulesSelectedEvent) e).getFired();
            } else if ( e.getSeverity() == FEELEvent.Severity.ERROR ) {
                MsgUtil.reportMessage( logger,
                                       DMNMessage.Severity.ERROR,
                                       ((DMNBaseNode)node).getSource(),
                                       result,
                                       null,
                                       e,
                                       Msg.FEEL_ERROR,
                                       e.getMessage() );
                r.hasErrors = true;
            } else if ( e.getSeverity() == FEELEvent.Severity.WARN ) {
                MsgUtil.reportMessage( logger,
                                       DMNMessage.Severity.WARN,
                                       ((DMNBaseNode)node).getSource(),
                                       result,
                                       null,
                                       e,
                                       Msg.FEEL_WARN,
                                       e.getMessage() );
            }
        }
        events.clear();
        return r;
    }

    private static class EventResults {
        public boolean hasErrors = false;
        public List<Integer> matchedRules;
        public List<Integer> fired;
    }

    @Override
    public void onEvent(FEELEvent event) {
        this.events.add( event );
    }
}
