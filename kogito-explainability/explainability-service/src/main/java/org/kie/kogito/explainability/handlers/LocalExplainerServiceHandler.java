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
package org.kie.kogito.explainability.handlers;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.kie.kogito.explainability.api.BaseExplainabilityRequest;
import org.kie.kogito.explainability.api.BaseExplainabilityResult;
import org.kie.kogito.explainability.local.LocalExplainer;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;

/**
 * A local explainability handler that delegates explanation to a {@link LocalExplainer} and handles assembly
 * of the {@link BaseExplainabilityResult} for both success and failure states of explanation calculation.
 *
 * @param <T> the type of local explanation generated
 * @param <R> the type of the local explanation request
 */
public interface LocalExplainerServiceHandler<T, R extends BaseExplainabilityRequest>
        extends LocalExplainer<T> {

    /**
     * Checks whether an implementation supports a type of explanation.
     *
     * @param type The Trusty Service request type.
     * @return true if the implementation supports the type of explanation.
     */
    <U extends BaseExplainabilityRequest> boolean supports(Class<U> type);

    /**
     * Gets a Prediction object from the request for the LocalExplainer. It should contain all the necessary
     * information for the LocalExplainer to calculate an explanation.
     *
     * @param request The explanation request.
     * @return A Prediction object containing all of the information necessary to calculate an explanation.
     */
    Prediction getPrediction(R request);

    /**
     * Gets a PredictionProvider object from the request for the LocalExplainer.
     *
     * @param request The explanation request.
     * @return A PredictionProvider object.
     */
    PredictionProvider getPredictionProvider(R request);

    /**
     * Requests calculation of an explanation decorated with both "success" and "failure" result handlers.
     * See:
     * - {@link LocalExplainer#explainAsync}
     * - {@link LocalExplainerServiceHandler#createSucceededResult(BaseExplainabilityRequest, Object)}
     * - {@link LocalExplainerServiceHandler#createFailedResult(BaseExplainabilityRequest, Throwable)}
     *
     * @param request The explanation request.
     * @param intermediateResultsConsumer A consumer for intermediate results provided by the explainer.
     * @return
     */
    default CompletableFuture<BaseExplainabilityResult> explainAsyncWithResults(R request,
            Consumer<BaseExplainabilityResult> intermediateResultsConsumer) {
        Prediction prediction = getPrediction(request);
        PredictionProvider predictionProvider = getPredictionProvider(request);
        return explainAsync(prediction,
                predictionProvider,
                s -> intermediateResultsConsumer.accept(createIntermediateResult(request, s)))
                        .thenApply(input -> createSucceededResult(request, input))
                        .exceptionally(e -> createFailedResult(request, e));
    }

    /**
     * Creates a result containing the "success" information for an explanation calculation.
     *
     * @param request The original request.
     * @param result The result from the LocalExplainer calculation.
     * @return
     */
    BaseExplainabilityResult createSucceededResult(R request, T result);

    /**
     * Creates a result containing the "failed" information for an explanation calculation.
     *
     * @param request The original request.
     * @param throwable The exception thrown during calculation.
     * @return
     */
    BaseExplainabilityResult createFailedResult(R request, Throwable throwable);

    /**
     * Creates a result containing the "intermediate" information for an explanation calculation.
     *
     * @param request The original request.
     * @param result The intermediate result from the LocalExplainer calculation.
     * @return
     */
    BaseExplainabilityResult createIntermediateResult(R request, T result);

}
