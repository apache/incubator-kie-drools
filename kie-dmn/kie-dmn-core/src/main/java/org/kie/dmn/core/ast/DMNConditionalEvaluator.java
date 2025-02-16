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

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.api.core.EvaluatorResult;
import org.kie.dmn.api.core.EvaluatorResult.ResultType;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.impl.DMNRuntimeEventManagerUtils;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.api.DMNElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNConditionalEvaluator implements DMNExpressionEvaluator {

    public enum EvaluatorType {
        IF,
        THEN,
        ELSE
    }

    public static class EvaluatorIdentifier {
        final String id;
        final EvaluatorType type;

        public EvaluatorIdentifier (String id, EvaluatorType type) {
            this.id = id;
            this.type = type;
        }

    }

    private static final Logger logger = LoggerFactory.getLogger(DMNConditionalEvaluator.class);

    private final DMNExpressionEvaluator ifEvaluator;
    private final DMNExpressionEvaluator thenEvaluator;
    private final DMNExpressionEvaluator elseEvaluator;
    private final DMNElement node;
    private final String name;
    private final Map<EvaluatorIdentifier, DMNExpressionEvaluator> evaluatorIdMap;
    private final EvaluatorIdentifier ifEvaluatorIdentifier;
    private final EvaluatorIdentifier thenEvaluatorIdentifier;
    private final EvaluatorIdentifier elseEvaluatorIdentifier;

    static Map<EvaluatorType, EvaluatorIdentifier> mapEvaluatorIdentifiers(Map<EvaluatorIdentifier, DMNExpressionEvaluator> evaluatorIdMap) {
        return evaluatorIdMap.keySet().stream()
                .collect(Collectors.toMap(identifier -> identifier.type, Function.identity()));
    }

    static EvaluatorIdentifier getEvaluatorIdentifier(Map<EvaluatorType, EvaluatorIdentifier> evaluatorIdentifierMap, EvaluatorType type) {
        return Optional.ofNullable(evaluatorIdentifierMap.get(type))
                .orElseThrow(() -> new RuntimeException("Missing " + type + " evaluator in evaluatorIdMap"));
    }

    public DMNConditionalEvaluator(String name, DMNElement node, Map <EvaluatorIdentifier, DMNExpressionEvaluator> evaluatorIdMap) {
        this.name = name;
        this.node = node;
        Map<EvaluatorType, EvaluatorIdentifier> evaluatorIdentifierMap = mapEvaluatorIdentifiers(evaluatorIdMap);
        this.ifEvaluatorIdentifier = getEvaluatorIdentifier(evaluatorIdentifierMap, EvaluatorType.IF);
        this.thenEvaluatorIdentifier = getEvaluatorIdentifier(evaluatorIdentifierMap, EvaluatorType.THEN);
        this.elseEvaluatorIdentifier = getEvaluatorIdentifier(evaluatorIdentifierMap, EvaluatorType.ELSE);
        this.ifEvaluator = evaluatorIdMap.get(ifEvaluatorIdentifier);
        this.thenEvaluator = evaluatorIdMap.get(thenEvaluatorIdentifier);
        this.elseEvaluator = evaluatorIdMap.get(elseEvaluatorIdentifier);
        this.evaluatorIdMap = evaluatorIdMap;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        DMNResultImpl result = (DMNResultImpl) dmnr;

        EvaluatorResult ifEvaluation = ifEvaluator.evaluate(eventManager, result);
        String executedId = ifEvaluatorIdentifier.id;
        DMNRuntimeEventManagerUtils.fireAfterEvaluateConditional(eventManager, ifEvaluation, executedId);
        if (ifEvaluation.getResultType().equals(ResultType.SUCCESS)) {
            Object ifResult = ifEvaluation.getResult();
            if (ifResult == null || ifResult instanceof Boolean) {
                return manageBooleanOrNullIfResult((Boolean) ifResult, eventManager, result);
            } else {
                MsgUtil.reportMessage(logger,
                                      DMNMessage.Severity.ERROR,
                                      node,
                                      result,
                                      null,
                                      null,
                                      Msg.CONDITION_RESULT_NOT_BOOLEAN,
                                      name,
                                      ifResult);
            }
        }

        return new EvaluatorResultImpl(null, ResultType.FAILURE);
    }

    protected EvaluatorResult manageBooleanOrNullIfResult(Boolean booleanResult, DMNRuntimeEventManager eventManager, DMNResultImpl result) {
        DMNExpressionEvaluator evaluatorToUse = booleanResult != null && booleanResult ? thenEvaluator : elseEvaluator;

        EvaluatorResult toReturn = evaluatorToUse.evaluate(eventManager, result);

        String executedId = evaluatorToUse.equals(thenEvaluator) ? thenEvaluatorIdentifier.id : elseEvaluatorIdentifier.id;

        DMNRuntimeEventManagerUtils.fireAfterConditionalEvaluation(eventManager, name, toReturn, executedId);
        return toReturn;
    }

}
