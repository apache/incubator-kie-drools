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
package org.kie.dmn.trisotech.core.ast;

import java.util.Collections;
import java.util.LinkedList;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.ast.EvaluatorResultImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.model.api.DMNElement;
import org.kie.dmn.trisotech.core.util.IterableRange;
import org.kie.dmn.trisotech.core.util.Msg;
import org.kie.dmn.trisotech.model.api.Iterator.IteratorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNIteratorEvaluator implements DMNExpressionEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(DMNIteratorEvaluator.class);

    private String variable;
    private IteratorType type;
    private DMNExpressionEvaluator inEvaluator;
    private DMNExpressionEvaluator returnEvaluator;
    private DMNElement node;
    private String name;

    public DMNIteratorEvaluator(String name, DMNElement node, IteratorType type, String variable, DMNExpressionEvaluator in, DMNExpressionEvaluator ret) {
        this.name = name;
        this.node = node;
        this.type = type;
        this.variable = variable;
        this.inEvaluator = in;
        this.returnEvaluator = ret;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        DMNResultImpl result = (DMNResultImpl) dmnr;

        EvaluatorResult inResult = inEvaluator.evaluate(eventManager, result);
        if (inResult == null || inResult.getResultType() != ResultType.SUCCESS) {
            return inResult;
        }
        Object inObj = inResult.getResult();

        if (inObj instanceof Range) {
            inObj = new IterableRange((Range) inObj);
        } else if (!(inObj instanceof Iterable)) {

            //Can't iterate on null
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

            //Coercion to list if a single result is returned 
            inObj = Collections.singletonList(inObj);
        }

        DMNContext previousContext = result.getContext();
        DMNContext dmnContext = previousContext.clone();

        LinkedList<Object> returnList = new LinkedList<>();
        try {
            result.setContext(dmnContext);
            dmnContext.set("partial", returnList);

            for (Object item : (Iterable) inObj) {
                dmnContext.set(variable, item);
                EvaluatorResult evaluate = returnEvaluator.evaluate(eventManager, dmnr);
                returnList.add(evaluate.getResult());
            }

        } finally {
            result.setContext(previousContext);
        }

        switch (type) {
            case EVERY:
                for (Object satisfies : returnList) {
                    if (!(satisfies instanceof Boolean) || ((Boolean) satisfies).booleanValue() == false) {
                        return new EvaluatorResultImpl(Boolean.FALSE, ResultType.SUCCESS);
                    }
                }
                return new EvaluatorResultImpl(Boolean.TRUE, ResultType.SUCCESS);

            case SOME:
                for (Object satisfies : returnList) {
                    if (satisfies instanceof Boolean && ((Boolean) satisfies).booleanValue() == true) {
                        return new EvaluatorResultImpl(Boolean.TRUE, ResultType.SUCCESS);
                    }
                }
                return new EvaluatorResultImpl(Boolean.FALSE, ResultType.SUCCESS);

            case FOR:
                return new EvaluatorResultImpl(returnList, ResultType.SUCCESS);
        }

        return new EvaluatorResultImpl(null, ResultType.FAILURE);

    }

}
