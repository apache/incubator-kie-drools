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

package org.kie.dmn.core.api;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;

/**
 * An Expression Evaluator interface for DMN defined expressions
 */
@FunctionalInterface
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
    EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult result);

}
