/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.compiler.alphanetbased;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.ast.DMNDTExpressionEvaluator;
import org.kie.dmn.core.ast.EvaluatorResultImpl;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.impl.DMNRuntimeEventManagerUtils;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.core.ast.DMNDTExpressionEvaluator.processEvents;

public class AlphaNetDMNExpressionEvaluator implements DMNExpressionEvaluator {

    private static Logger logger = LoggerFactory.getLogger( AlphaNetDMNExpressionEvaluator.class );

    private final DMNCompiledAlphaNetwork compiledNetwork;

    private DMNFEELHelper feel;
    private String decisionTableName;
    private DMNBaseNode node;

    public AlphaNetDMNExpressionEvaluator( DMNCompiledAlphaNetwork compiledNetwork ) {
        this.compiledNetwork = compiledNetwork;
    }

    @Override
    public EvaluatorResult evaluate( DMNRuntimeEventManager eventManager, DMNResult dmnResult ) {
        List<FEELEvent> events = new ArrayList<>();
        DMNRuntimeEventManagerUtils.fireBeforeEvaluateDecisionTable(eventManager, node.getName(), decisionTableName, dmnResult);

        EvaluationContext evalCtx = createEvaluationContext(events, eventManager, dmnResult);
        evalCtx.enterFrame();

        DMNDTExpressionEvaluator.EventResults eventResults = null;
        try {
            Object result = compiledNetwork.evaluate(evalCtx);

            eventResults = processEvents(events, eventManager, (DMNResultImpl) dmnResult, node);

            return new EvaluatorResultImpl(result,
                                           eventResults.hasErrors ?
                                                   EvaluatorResult.ResultType.FAILURE :
                                                   EvaluatorResult.ResultType.SUCCESS);
        } catch (RuntimeException e) {
            logger.error(e.toString(), e);
            throw e;
        } finally {
            evalCtx.exitFrame();
            DMNRuntimeEventManagerUtils.fireAfterEvaluateDecisionTable(eventManager, node.getName(), decisionTableName, dmnResult,
                                                                       (eventResults != null ? eventResults.matchedRules : null), (eventResults != null ? eventResults.fired : null));
        }
    }

    private EvaluationContext createEvaluationContext( List<FEELEvent> events, DMNRuntimeEventManager eventManager, DMNResult dmnResult ) {
        EvaluationContextImpl ctx = feel.newEvaluationContext( Collections.singletonList( events::add ), Collections.emptyMap());
        ctx.setPerformRuntimeTypeCheck((( DMNRuntimeImpl ) eventManager.getRuntime()).performRuntimeTypeCheck(( ( DMNResultImpl ) dmnResult).getModel()));
        ctx.setValues( dmnResult.getContext().getAll() );
        return ctx;
    }

    public AlphaNetDMNExpressionEvaluator initParameters(DMNFEELHelper feel, DMNCompilerContext ctx, String decisionTableName, DMNBaseNode node) {
        this.feel = feel;
        this.decisionTableName = decisionTableName;
        this.node = node;
        return this;
    }
}
