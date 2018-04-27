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

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;

/**
 * An evaluator that wraps a delegated DMNExpressionEvaluator, which is called for a specific scope name of the DMNContext.
 */
public class DMNWalksIntoScopeEvaluator implements DMNExpressionEvaluator {

    private DMNExpressionEvaluator delegate;
    private String scopeName;

    public DMNWalksIntoScopeEvaluator(DMNExpressionEvaluator delegate, String scopeName) {
        this.delegate = delegate;
        this.scopeName = scopeName;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager dmrem, DMNResult result) {
        result.getContext().pushScope(scopeName);
        EvaluatorResult evResult = delegate.evaluate(dmrem, result);
        result.getContext().popScope();
        return evResult;
    }
}
