/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.handlers;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.kogito.explainability.PredictionProviderFactory;
import org.kie.kogito.explainability.api.BaseExplainabilityRequestDto;
import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.explainability.local.LocalExplainer;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.models.BaseExplainabilityRequest;

@ApplicationScoped
public class LocalExplainerServiceHandlerRegistry {

    public static final String SERVICE_HANDLER_NOT_FOUND_ERROR_MESSAGE = "LocalExplainerServiceHandler could not be found for '%s'";

    private PredictionProviderFactory predictionProviderFactory;
    private Instance<LocalExplainerServiceHandler<?, ?, ?>> explanationHandlers;

    protected LocalExplainerServiceHandlerRegistry() {
        //CDI proxy
    }

    @Inject
    public LocalExplainerServiceHandlerRegistry(PredictionProviderFactory predictionProviderFactory,
            @Any Instance<LocalExplainerServiceHandler<?, ?, ?>> explanationHandlers) {
        this.predictionProviderFactory = predictionProviderFactory;
        this.explanationHandlers = explanationHandlers;
    }

    /**
     * Converts the result from the Explainability Service to that used by Trusty Service.
     *
     * @param dto The request coming from the Trusty Service
     * @return The request used by Explainability Service
     */
    public <R extends BaseExplainabilityRequest, D extends BaseExplainabilityRequestDto> R explainabilityRequestFrom(D dto) {
        LocalExplainerServiceHandler<?, ?, ?> explanationHandler = getLocalExplainerDto(dto.getClass())
                .orElseThrow(() -> new IllegalArgumentException(String.format(SERVICE_HANDLER_NOT_FOUND_ERROR_MESSAGE, dto.getClass().getName())));

        return cast(explanationHandler.explainabilityRequestFrom(castDto(dto)));
    }

    /**
     * Requests calculation of an explanation decorated with both "success" and "failure" result handlers.
     * See:
     * - {@link LocalExplainer#explainAsync}
     * - {@link LocalExplainerServiceHandler#createSucceededResultDto(BaseExplainabilityRequest, Object)}
     * - {@link LocalExplainerServiceHandler#createFailedResultDto(BaseExplainabilityRequest, Throwable)}
     *
     * @param request The explanation request.
     * @param intermediateResultsConsumer A consumer of intermediate results provided by the explainer.
     * @return
     */
    public <R extends BaseExplainabilityRequest, S extends BaseExplainabilityResultDto> CompletableFuture<BaseExplainabilityResultDto> explainAsyncWithResults(R request,
            Consumer<S> intermediateResultsConsumer) {

        LocalExplainerServiceHandler<?, ?, ?> explanationHandler = getLocalExplainer(request.getClass())
                .orElseThrow(() -> new IllegalArgumentException(String.format(SERVICE_HANDLER_NOT_FOUND_ERROR_MESSAGE, request.getClass().getName())));

        try {
            PredictionProvider predictionProvider = predictionProviderFactory.createPredictionProvider(request);
            return explanationHandler.explainAsyncWithResults(cast(request),
                    predictionProvider,
                    castConsumer(intermediateResultsConsumer));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(explanationHandler.createFailedResultDto(cast(request), e));
        }
    }

    private <T extends BaseExplainabilityRequest> Optional<LocalExplainerServiceHandler<?, ?, ?>> getLocalExplainer(Class<T> type) {
        return this.explanationHandlers.stream().filter(explainer -> explainer.supports(type)).findFirst();
    }

    private <T extends BaseExplainabilityRequestDto> Optional<LocalExplainerServiceHandler<?, ?, ?>> getLocalExplainerDto(Class<T> type) {
        return this.explanationHandlers.stream().filter(explainer -> explainer.supportsDto(type)).findFirst();
    }

    @SuppressWarnings("unchecked")
    private <T extends BaseExplainabilityRequest> T cast(BaseExplainabilityRequest type) {
        return (T) type;
    }

    @SuppressWarnings("unchecked")
    private <T extends BaseExplainabilityRequestDto> T castDto(BaseExplainabilityRequestDto type) {
        return (T) type;
    }

    @SuppressWarnings("unchecked")
    private <S extends BaseExplainabilityResultDto> Consumer<S> castConsumer(Consumer<?> consumer) {
        return (Consumer<S>) consumer;
    }

}
