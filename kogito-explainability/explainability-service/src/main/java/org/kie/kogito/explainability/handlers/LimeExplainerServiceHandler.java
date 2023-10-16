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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.explainability.PredictionProviderFactory;
import org.kie.kogito.explainability.api.BaseExplainabilityRequest;
import org.kie.kogito.explainability.api.BaseExplainabilityResult;
import org.kie.kogito.explainability.api.FeatureImportanceModel;
import org.kie.kogito.explainability.api.LIMEExplainabilityRequest;
import org.kie.kogito.explainability.api.LIMEExplainabilityResult;
import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.explainability.api.SaliencyModel;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.model.SimplePrediction;

import static org.kie.kogito.explainability.ConversionUtils.toFeatureList;
import static org.kie.kogito.explainability.ConversionUtils.toOutputList;

@ApplicationScoped
public class LimeExplainerServiceHandler implements LocalExplainerServiceHandler<Map<String, Saliency>, LIMEExplainabilityRequest> {

    private final LimeExplainer explainer;
    private final PredictionProviderFactory predictionProviderFactory;

    @Inject
    public LimeExplainerServiceHandler(LimeExplainer explainer,
            PredictionProviderFactory predictionProviderFactory) {
        this.explainer = explainer;
        this.predictionProviderFactory = predictionProviderFactory;
    }

    @Override
    public <T extends BaseExplainabilityRequest> boolean supports(Class<T> type) {
        return LIMEExplainabilityRequest.class.isAssignableFrom(type);
    }

    @Override
    public PredictionProvider getPredictionProvider(LIMEExplainabilityRequest request) {
        return predictionProviderFactory.createPredictionProvider(request.getServiceUrl(),
                request.getModelIdentifier(),
                request.getOutputs());
    }

    @Override
    public Prediction getPrediction(LIMEExplainabilityRequest request) {
        Collection<NamedTypedValue> inputs = request.getInputs();
        Collection<NamedTypedValue> outputs = request.getOutputs();

        PredictionInput input = new PredictionInput(toFeatureList(inputs));
        PredictionOutput output = new PredictionOutput(toOutputList(outputs));
        return new SimplePrediction(input, output);
    }

    @Override
    public BaseExplainabilityResult createSucceededResult(LIMEExplainabilityRequest request,
            Map<String, Saliency> result) {
        return LIMEExplainabilityResult.buildSucceeded(
                request.getExecutionId(),
                result.entrySet().stream()
                        .map(e -> new SaliencyModel(e.getKey(), e.getValue().getPerFeatureImportance().stream()
                                .map(f -> new FeatureImportanceModel(f.getFeature().getName(),
                                        f.getScore()))
                                .collect(Collectors.toList())))
                        .collect(Collectors.toList()));
    }

    @Override
    public BaseExplainabilityResult createIntermediateResult(LIMEExplainabilityRequest request, Map<String, Saliency> result) {
        throw new UnsupportedOperationException("Intermediate results are not supported by LIME.");
    }

    @Override
    public BaseExplainabilityResult createFailedResult(LIMEExplainabilityRequest request, Throwable throwable) {
        return LIMEExplainabilityResult.buildFailed(request.getExecutionId(), throwable.getMessage());
    }

    @Override
    public CompletableFuture<Map<String, Saliency>> explainAsync(Prediction prediction,
            PredictionProvider predictionProvider,
            Consumer<Map<String, Saliency>> intermediateResultsConsumer) {
        return explainer.explainAsync(prediction,
                predictionProvider,
                intermediateResultsConsumer);
    }
}
