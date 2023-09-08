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
package org.kie.dmn.core.compiler.alphanetbased;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.ast.DMNDTExpressionEvaluator;
import org.kie.dmn.core.ast.EvaluatorResultImpl;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.impl.DMNRuntimeEventManagerUtils;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.runtime.events.InvalidInputEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.core.ast.DMNDTExpressionEvaluator.processEvents;

public class DMNAlphaNetworkEvaluatorImpl implements DMNExpressionEvaluator {

    private static Logger logger = LoggerFactory.getLogger(DMNAlphaNetworkEvaluatorImpl.class);

    private final DMNAlphaNetworkEvaluator compiledNetwork;
    private Results results;
    private final DMNFEELHelper feel;
    private final String decisionTableName;
    private final String decisionTableId;
    private final FeelDecisionTable feelDecisionTable;
    private final DMNBaseNode node;

    public DMNAlphaNetworkEvaluatorImpl(DMNAlphaNetworkEvaluator compiledNetwork,
                                        DMNFEELHelper feel,
                                        String decisionTableName,
                                        String decisionTableId,
                                        FeelDecisionTable feelDecisionTable,
                                        DMNBaseNode node,
                                        Results results) {
        this.feel = feel;
        this.decisionTableName = decisionTableName;
        this.feelDecisionTable = feelDecisionTable;
        this.decisionTableId = decisionTableId;
        this.node = node;
        this.compiledNetwork = compiledNetwork;
        this.results = results;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult dmnResult) {
        DMNRuntimeEventManagerUtils.fireBeforeEvaluateDecisionTable(eventManager, node.getName(), decisionTableName, decisionTableId, dmnResult);

        EvaluationContext evalCtx = createEvaluationContext(results.getEvents(), eventManager, dmnResult);
        evalCtx.enterFrame();

        DMNDTExpressionEvaluator.EventResults eventResults = null;
        try {

            Optional<InvalidInputEvent> potentialError = compiledNetwork.validate(evalCtx);
            if (potentialError.isPresent()) {
                InvalidInputEvent actualError = potentialError.get();
                MsgUtil.reportMessage(logger,
                                      DMNMessage.Severity.ERROR,
                                      node.getSource(),
                                      (DMNResultImpl) dmnResult,
                                      null,
                                      actualError,
                                      Msg.FEEL_ERROR,
                                      actualError.getMessage());
                return new EvaluatorResultImpl(null, EvaluatorResult.ResultType.FAILURE);
            }

            Object result = compiledNetwork.evaluate(evalCtx, feelDecisionTable);

            eventResults = processEvents(results.getEvents(), eventManager, (DMNResultImpl) dmnResult, node);

            return new EvaluatorResultImpl(result,
                                           eventResults.hasErrors ?
                                                   EvaluatorResult.ResultType.FAILURE :
                                                   EvaluatorResult.ResultType.SUCCESS);
        } catch (RuntimeException e) {
            logger.error(e.toString(), e);
            throw e;
        } finally {
            evalCtx.exitFrame();
            DMNRuntimeEventManagerUtils.fireAfterEvaluateDecisionTable(eventManager, node.getName(), decisionTableName, decisionTableId, dmnResult,
                                                                       (eventResults != null ? eventResults.matchedRules : null), (eventResults != null ? eventResults.fired : null));
        }
    }

    private EvaluationContext createEvaluationContext(List<FEELEvent> events, DMNRuntimeEventManager eventManager, DMNResult dmnResult) {
        EvaluationContextImpl ctx = feel.newEvaluationContext(Collections.singletonList(events::add), Collections.emptyMap());
        ctx.setPerformRuntimeTypeCheck(((DMNRuntimeImpl) eventManager.getRuntime()).performRuntimeTypeCheck(((DMNResultImpl) dmnResult).getModel()));
        ctx.setValues(dmnResult.getContext().getAll());
        return ctx;
    }
}
