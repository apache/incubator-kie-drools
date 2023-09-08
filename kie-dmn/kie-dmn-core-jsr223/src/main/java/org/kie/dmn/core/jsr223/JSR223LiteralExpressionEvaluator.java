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
package org.kie.dmn.core.jsr223;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.ast.EvaluatorResultImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSR223LiteralExpressionEvaluator implements DMNExpressionEvaluator {
    
    private static final Logger LOG = LoggerFactory.getLogger(JSR223LiteralExpressionEvaluator.class);
    
    private final JSR223ScriptEngineEvaluator eval;

    public JSR223LiteralExpressionEvaluator(JSR223ScriptEngineEvaluator eval) {
        this.eval = eval;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        DMNResultImpl result = (DMNResultImpl) dmnr;
        Object evaluatorResult = null;
        ResultType resultType = ResultType.SUCCESS;
        try {
            evaluatorResult = eval.eval(result.getContext().getAll());
        } catch (Exception e) {
            LOG.debug("failed literal evaluate", e);
            resultType = ResultType.FAILURE;
        }
        return new EvaluatorResultImpl(evaluatorResult, resultType);
    }
    
    public JSR223ScriptEngineEvaluator getEval() {
        return eval;
    }

}
