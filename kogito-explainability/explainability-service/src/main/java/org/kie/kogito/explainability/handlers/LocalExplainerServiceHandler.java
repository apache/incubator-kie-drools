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

import java.util.concurrent.CompletableFuture;

import org.kie.kogito.explainability.api.BaseExplainabilityRequestDto;
import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.explainability.local.LocalExplainer;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.models.BaseExplainabilityRequest;

/**
 * A local explainability handler that delegates explanation to a {@link LocalExplainer} and handles assembly
 * of the {@link BaseExplainabilityResultDto} for both success and failure states of explanation calculation.
 *
 * @param <R> the type of the local explanation request
 * @param <T> the type of local explanation generated
 */
public interface LocalExplainerServiceHandler<T, R extends BaseExplainabilityRequest, D extends BaseExplainabilityRequestDto>
        extends LocalExplainer<T> {

    /**
     * Checks whether an implementation supports a type of explanation.
     *
     * @param type The Trusty Service request type.
     * @param <T>
     * @return true if the implementation supports the type of explanation.
     */
    // See https://issues.redhat.com/browse/FAI-457
    // We will not need the overloaded generic class when we only have one type of DTO
    <T extends BaseExplainabilityRequest> boolean supports(Class<T> type);

    /**
     * Checks whether an implementation supports a type of explanation.
     *
     * @param type The Explainability Service result type.
     * @param <T>
     * @return true if the implementation supports the type of explanation.
     */
    // See https://issues.redhat.com/browse/FAI-457
    <T extends BaseExplainabilityRequestDto> boolean supportsDto(Class<T> type);

    /**
     * Converts the result from the Explainability Service to that used by Trusty Service.
     *
     * @param dto The request coming from the Trusty Service
     * @return The request used by Explainability Service
     */
    R explainabilityRequestFrom(D dto);

    /**
     * Gets a Prediction object from the request for the LocalExplainer. It should contain all the necessary
     * information for the LocalExplainer to calculate an explanation.
     *
     * @param request The explanation request.
     * @return A Prediction object containing all of the information necessary to calculate an explanation.
     */
    Prediction getPrediction(R request);

    /**
     * Requests calculation of an explanation decorated with both "success" and "failure" result handlers.
     * See:
     * - {@link LocalExplainer#explainAsync}
     * - {@link LocalExplainerServiceHandler#createSucceededResultDto(BaseExplainabilityRequest, Object)}
     * - {@link LocalExplainerServiceHandler#createFailedResultDto(BaseExplainabilityRequest, Throwable)}
     *
     * @param request The explanation request.
     * @param model The prediction model to explain. See {@link PredictionProvider}
     * @return
     */
    default CompletableFuture<BaseExplainabilityResultDto> explainAsyncWithResults(R request,
            PredictionProvider model) {
        Prediction prediction = getPrediction(request);
        return explainAsync(prediction, model)
                .thenApply(input -> createSucceededResultDto(request, input))
                .exceptionally(e -> createFailedResultDto(request, e));
    }

    /**
     * Creates a DTO containing the "success" information for an explanation calculation.
     *
     * @param request The original request.
     * @param result The result from the LocalExplainer calculation.
     * @return
     */
    BaseExplainabilityResultDto createSucceededResultDto(R request, T result);

    /**
     * Creates a DTO containing the "failed" information for an explanation calculation.
     *
     * @param request The original request.
     * @param throwable The exception thrown during calculation.
     * @return
     */
    BaseExplainabilityResultDto createFailedResultDto(R request, Throwable throwable);

}
