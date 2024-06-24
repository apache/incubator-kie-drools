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
import java.util.Map;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.util.IterableRange;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.model.api.DMNElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNFilterEvaluator implements DMNExpressionEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(DMNFilterEvaluator.class);

    private DMNExpressionEvaluator inEvaluator;
    private DMNExpressionEvaluator filterEvaluator;
    private DMNElement node;
    private String name;

    public DMNFilterEvaluator(String name, DMNElement node, DMNExpressionEvaluator inEvaluator, DMNExpressionEvaluator filterEvaluator) {
        this.name = name;
        this.node = node;
        this.inEvaluator = inEvaluator;
        this.filterEvaluator = filterEvaluator;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        DMNResultImpl result = (DMNResultImpl) dmnr;

        if (inEvaluator == null || filterEvaluator == null) {
            return new EvaluatorResultImpl(null, ResultType.FAILURE);
        }

        EvaluatorResult inResult = inEvaluator.evaluate(eventManager, result);
        if (inResult.getResultType() != ResultType.SUCCESS) {
            return inResult;
        }
        Object inObj = inResult.getResult();

        if (inObj instanceof Range range) {
            inObj = new IterableRange(range);
        } else if (!(inObj instanceof Iterable)) {
            if (inObj == null) {
                MsgUtil.reportMessage(logger,
                                      DMNMessage.Severity.ERROR,
                                      node,
                                      result,
                                      null,
                                      null,
                                      Msg.IN_RESULT_NULL,
                                      name);
                return new EvaluatorResultImpl(null, ResultType.FAILURE);
            }

            // 10.3.2.9.4 Type conversions "to singleton list"
            inObj = Collections.singletonList(inObj);
        }

        DMNContext previousContext = result.getContext();
        DMNContext dmnContext = previousContext.clone();

        List<Object> returnList = new ArrayList<>();
        try {
            result.setContext(dmnContext);

            for (Object item : (Iterable<?>) inObj) {

                dmnContext.set("item", item);
                if (item instanceof Map) {
                    Map<String, Object> complexItem = (Map<String, Object>) item;
                    complexItem.forEach(dmnContext::set);
                }

                EvaluatorResult evaluate = filterEvaluator.evaluate(eventManager, dmnr);
                Object evalReturn = evaluate.getResult();

                //If the evaluation is a boolean result, we add the item based on a return of true
                if (evalReturn instanceof Boolean booleanResult) {
                    if (Boolean.TRUE.equals(booleanResult)) {
                        returnList.add(item);
                    }
                } else {
                    MsgUtil.reportMessage(logger,
                            DMNMessage.Severity.ERROR,
                            node,
                            result,
                            null,
                            null,
                            Msg.FILTER_EXPRESSION_RESULT_NOT_BOOLEAN,
                            name);
                    return new EvaluatorResultImpl(null, ResultType.FAILURE);
                }
            }

        } finally {
            result.setContext(previousContext);
        }

        return new EvaluatorResultImpl(returnList, ResultType.SUCCESS);
    }

}
