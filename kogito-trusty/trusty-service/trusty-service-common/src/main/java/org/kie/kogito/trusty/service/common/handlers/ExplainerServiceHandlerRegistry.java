/*
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
package org.kie.kogito.trusty.service.common.handlers;

import java.util.Optional;

import org.kie.kogito.explainability.api.BaseExplainabilityResult;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class ExplainerServiceHandlerRegistry {

    private Instance<ExplainerServiceHandler<?>> explanationHandlers;

    protected ExplainerServiceHandlerRegistry() {
        //CDI proxy
    }

    @Inject
    public ExplainerServiceHandlerRegistry(@Any Instance<ExplainerServiceHandler<?>> explanationHandlers) {
        this.explanationHandlers = explanationHandlers;
    }

    /**
     * Gets the result for an explanation.
     *
     * @param executionId The execution Id for which to retrieve an explanation result.
     * @param type The type of result to retrieve.
     * @return The result of an explanation.
     */
    public <T extends BaseExplainabilityResult> T getExplainabilityResultById(String executionId, Class<T> type) {
        ExplainerServiceHandler<?> explanationHandler = getLocalExplainer(type)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Explainability result for '%s' is not supported", type.getName())));

        return cast(explanationHandler.getExplainabilityResultById(executionId));
    }

    /**
     * Stores the result for an explanation.
     *
     * @param executionId The execution Id for which to retrieve an explanation result.
     * @param result The result to store.
     */
    public <T extends BaseExplainabilityResult> void storeExplainabilityResult(String executionId, T result) {
        T type = cast(result);
        ExplainerServiceHandler<?> explanationHandler =
                getLocalExplainer(type.getClass())
                        .orElseThrow(() -> new IllegalArgumentException(String.format("Explainability result for '%s' is not supported", type.getClass().getName())));

        explanationHandler.storeExplainabilityResult(executionId, cast(result));
    }

    private <T extends BaseExplainabilityResult> Optional<ExplainerServiceHandler<?>> getLocalExplainer(Class<T> type) {
        return this.explanationHandlers.stream().filter(handler -> handler.supports(type)).findFirst();
    }

    @SuppressWarnings("unchecked")
    private <T extends BaseExplainabilityResult> T cast(BaseExplainabilityResult type) {
        return (T) type;
    }
}
