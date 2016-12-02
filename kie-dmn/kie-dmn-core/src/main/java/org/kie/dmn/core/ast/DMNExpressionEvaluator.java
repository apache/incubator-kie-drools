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

import org.kie.dmn.core.api.event.InternalDMNRuntimeEventManager;
import org.kie.dmn.core.impl.DMNResultImpl;

/**
 * An Expression Evaluator interface for DMN defined expressions
 */
public interface DMNExpressionEvaluator {
    /**
     * Evaluates the expression, returning its result type (SUCCESS/FAILURE) and
     * result value.
     *
     * @param eventManager events manager to whom events are notified
     * @param result the result context instance
     *
     * @return the result of the evaluation of the expression
     */
    EvaluatorResult evaluate(InternalDMNRuntimeEventManager eventManager, DMNResultImpl result);

    enum ResultType {
        SUCCESS, FAILURE;
    }

    class EvaluatorResult {
        private final Object     result;
        private final ResultType code;

        public EvaluatorResult(Object result, ResultType code) {
            this.result = result;
            this.code = code;
        }

        public Object getResult() {
            return result;
        }

        public ResultType getResultType() {
            return code;
        }
    }
}
