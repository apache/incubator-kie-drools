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
package org.kie.kogito.explainability;

import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.explainability.api.BaseExplainabilityRequest;
import org.kie.kogito.explainability.api.BaseExplainabilityResult;
import org.kie.kogito.explainability.handlers.LocalExplainerServiceHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ExplanationServiceImpl implements ExplanationService {

    private static final Logger LOG = LoggerFactory.getLogger(ExplanationServiceImpl.class);

    private final LocalExplainerServiceHandlerRegistry explainerServiceHandlerRegistry;

    @Inject
    public ExplanationServiceImpl(LocalExplainerServiceHandlerRegistry explainerServiceHandlerRegistry) {
        this.explainerServiceHandlerRegistry = explainerServiceHandlerRegistry;
    }

    @Override
    public CompletionStage<BaseExplainabilityResult> explainAsync(BaseExplainabilityRequest request) {
        return explainAsync(request,
                baseExplainabilityResult -> {
                    /* NOP */});
    }

    @Override
    public CompletionStage<BaseExplainabilityResult> explainAsync(BaseExplainabilityRequest request,
            Consumer<BaseExplainabilityResult> intermediateResultConsumer) {
        LOG.debug("Explainability request {} with executionId {} for model {}:{}",
                request.getClass().getSimpleName(),
                request.getExecutionId(),
                request.getModelIdentifier().getResourceType(),
                request.getModelIdentifier().getResourceId());

        return explainerServiceHandlerRegistry.explainAsyncWithResults(request, intermediateResultConsumer);
    }
}
