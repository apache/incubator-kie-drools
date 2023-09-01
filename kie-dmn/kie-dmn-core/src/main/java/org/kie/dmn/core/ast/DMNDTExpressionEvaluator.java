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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.impl.DMNRuntimeEventManagerUtils;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.FEELImpl;
import org.kie.dmn.feel.runtime.FEELFunction.Param;
import org.kie.dmn.feel.runtime.events.DecisionTableRulesMatchedEvent;
import org.kie.dmn.feel.runtime.events.DecisionTableRulesSelectedEvent;
import org.kie.dmn.feel.runtime.functions.DTInvokerFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An evaluator for DMN Decision Table Expressions
 */
public class DMNDTExpressionEvaluator
        implements DMNExpressionEvaluator {
    private static Logger logger = LoggerFactory.getLogger( DMNDTExpressionEvaluator.class );

    private final DMNNode           node;
    private       DTInvokerFunction dt;
    private       FEELImpl          feel;
    private       String            dtNodeId;

    public DMNDTExpressionEvaluator(DMNNode node, FEEL feel, DTInvokerFunction dt) {
        this.node = node;
        this.dt = dt;
        this.feel = (FEELImpl) feel;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager dmrem, DMNResult dmnr) {

        final List<FEELEvent> events = new ArrayList<>();

        DMNResultImpl result = (DMNResultImpl) dmnr;
        EventResults r = null;
        try {
            DMNRuntimeEventManagerUtils.fireBeforeEvaluateDecisionTable( dmrem, node.getName(), dt.getName(), dtNodeId, result );
            List<String> paramNames = dt.getParameters().get(0).stream().map(Param::getName).collect(Collectors.toList());
            Object[] params = new Object[paramNames.size()];
            EvaluationContextImpl ctx = feel.newEvaluationContext(List.of(events::add), Collections.emptyMap());
            ctx.setPerformRuntimeTypeCheck(((DMNRuntimeImpl) dmrem.getRuntime()).performRuntimeTypeCheck(result.getModel()));

            Map<String, Object> contextValues = result.getContext().getAll();
            ctx.enterFrame((int) Math.ceil((contextValues.size() + params.length) / 0.75));
            // need to set the values for in context variables...
            for (Map.Entry<String, Object> entry : contextValues.entrySet()) {
                ctx.setValue( entry.getKey(), entry.getValue() );
            }
            EvaluationContext dtContext = ctx.current();
            for ( int i = 0; i < params.length; i++ ) {
                EvaluationContext evalCtx = ctx.current();
                evalCtx.enterFrame();
                params[i] = feel.evaluate(dt.getDecisionTable().getCompiledParameterNames().get(i), evalCtx);
                ctx.setValue( paramNames.get( i ), params[i] );
            }
            Object dtr = dt.invoke( dtContext, params ).cata( e -> { events.add( e); return null; }, Function.identity());

            // since ctx is a local variable that will be discarded, no need for a try/finally,
            // but still wanted to match the enter/exit frame for future maintainability purposes
            ctx.exitFrame();

            r = processEvents( events, dmrem, result, node );
            return new EvaluatorResultImpl( dtr, r.hasErrors ? ResultType.FAILURE : ResultType.SUCCESS );
        } finally {
            DMNRuntimeEventManagerUtils.fireAfterEvaluateDecisionTable( dmrem, node.getName(), dt.getName(), dtNodeId, result, (r != null ? r.matchedRules : null), (r != null ? r.fired : null) );
        }
    }

    public static EventResults processEvents(List<FEELEvent> events, DMNRuntimeEventManager eventManager, DMNResultImpl result, DMNNode node) {
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

    public static class EventResults {
        public boolean hasErrors = false;
        public List<Integer> matchedRules;
        public List<Integer> fired;
    }
    

    public String getDtNodeId() {
        return dtNodeId;
    }

    public void setDtNodeId(String dtNodeId) {
        this.dtNodeId = dtNodeId;
    }
}
