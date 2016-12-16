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

import org.kie.dmn.core.api.DMNMessage;
import org.kie.dmn.core.api.event.InternalDMNRuntimeEventManager;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.FEELImpl;
import org.kie.dmn.feel.runtime.events.DecisionTableRulesMatchedEvent;
import org.kie.dmn.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.runtime.functions.DTInvokerFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * An evaluator for DMN Decision Table Expressions
 */
public class DMNDTExpressionEvaluator
        implements DMNExpressionEvaluator, FEELEventListener {
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
    public EvaluatorResult evaluate(InternalDMNRuntimeEventManager eventManager, DMNResultImpl result) {
        EventResults r = null;
        try {
            eventManager.fireBeforeEvaluateDecisionTable( dt.getName(), result );
            List<String> paramNames = dt.getParameterNames().get( 0 );
            Object[] params = new Object[paramNames.size()];
            EvaluationContextImpl ctx = new EvaluationContextImpl( feel.getEventsManager() );
            for ( int i = 0; i < params.length; i++ ) {
                params[i] = feel.evaluate( paramNames.get( i ), result.getContext().getAll() );
                ctx.setValue( paramNames.get( i ), params[i] );
            }
            Object dtr = dt.invoke( ctx, params ).cata( e -> { events.add( e); return null; }, Function.identity());
            r = processEvents( events, eventManager, result );
            return new EvaluatorResult( dtr, r.hasErrors ? ResultType.FAILURE : ResultType.SUCCESS );
        } finally {
            eventManager.fireAfterEvaluateDecisionTable( dt.getName(), result, (r != null ? r.matchedRules : null) );
        }
    }

    private EventResults processEvents(List<FEELEvent> events, InternalDMNRuntimeEventManager eventManager, DMNResultImpl result) {
        EventResults r = new EventResults();
        for ( FEELEvent e : events ) {
            if ( e instanceof DecisionTableRulesMatchedEvent ) {
                r.matchedRules = ((DecisionTableRulesMatchedEvent) e).getMatches();
            } else if ( e.getSeverity() == FEELEvent.Severity.ERROR ) {
                result.addMessage( DMNMessage.Severity.ERROR, e.getMessage(), node.getId(), e );
                r.hasErrors = true;
            }
        }
        events.clear();
        return r;
    }

    private static class EventResults {
        public boolean hasErrors = false;
        public List<Integer> matchedRules;
    }

    @Override
    public void onEvent(FEELEvent event) {
        this.events.add( event );
    }
}
