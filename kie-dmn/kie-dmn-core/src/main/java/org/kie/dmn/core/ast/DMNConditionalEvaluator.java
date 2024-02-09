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

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.api.DMNElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNConditionalEvaluator implements DMNExpressionEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(DMNConditionalEvaluator.class);

    private DMNExpressionEvaluator ifEvaluator;
    private DMNExpressionEvaluator thenEvaluator;
    private DMNExpressionEvaluator elseEvaluator;
    private DMNElement node;
    private String name;

    public DMNConditionalEvaluator(String name, DMNElement node, DMNExpressionEvaluator ifEvaluator, DMNExpressionEvaluator thenEvaluator, DMNExpressionEvaluator elseEvaluator) {
        this.name = name;
        this.node = node;
        this.ifEvaluator = ifEvaluator;
        this.thenEvaluator = thenEvaluator;
        this.elseEvaluator = elseEvaluator;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        DMNResultImpl result = (DMNResultImpl) dmnr;

        EvaluatorResult ifEvaluation = ifEvaluator.evaluate(eventManager, result);
        if (ifEvaluation.getResultType().equals(ResultType.SUCCESS)) {
            Object ifResult = ifEvaluation.getResult();
            if (ifResult instanceof Boolean) {
                if (((Boolean) ifResult).booleanValue()) {
                    return thenEvaluator.evaluate(eventManager, result);
                } else {
                    return elseEvaluator.evaluate(eventManager, result);
                }
            } else if (ifResult == null) {
                return elseEvaluator.evaluate(eventManager, result);
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

}
