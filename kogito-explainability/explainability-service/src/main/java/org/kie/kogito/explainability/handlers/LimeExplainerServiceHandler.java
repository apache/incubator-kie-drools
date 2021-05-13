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

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.explainability.api.BaseExplainabilityRequestDto;
import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.explainability.api.FeatureImportanceDto;
import org.kie.kogito.explainability.api.LIMEExplainabilityRequestDto;
import org.kie.kogito.explainability.api.LIMEExplainabilityResultDto;
import org.kie.kogito.explainability.api.SaliencyDto;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.model.SimplePrediction;
import org.kie.kogito.explainability.models.BaseExplainabilityRequest;
import org.kie.kogito.explainability.models.LIMEExplainabilityRequest;
import org.kie.kogito.explainability.models.ModelIdentifier;
import org.kie.kogito.tracing.typedvalue.TypedValue;

import static org.kie.kogito.explainability.ConversionUtils.toFeatureList;
import static org.kie.kogito.explainability.ConversionUtils.toOutputList;

@ApplicationScoped
public class LimeExplainerServiceHandler implements LocalExplainerServiceHandler<Map<String, Saliency>, LIMEExplainabilityRequest, LIMEExplainabilityRequestDto> {

    private final LimeExplainer explainer;

    @Inject
    public LimeExplainerServiceHandler(LimeExplainer explainer) {
        this.explainer = explainer;
    }

    @Override
    public <T extends BaseExplainabilityRequest> boolean supports(Class<T> type) {
        return LIMEExplainabilityRequest.class.isAssignableFrom(type);
    }

    @Override
    public <T extends BaseExplainabilityRequestDto> boolean supportsDto(Class<T> type) {
        return LIMEExplainabilityRequestDto.class.isAssignableFrom(type);
    }

    @Override
    public LIMEExplainabilityRequest explainabilityRequestFrom(LIMEExplainabilityRequestDto dto) {
        return new LIMEExplainabilityRequest(
                dto.getExecutionId(),
                dto.getServiceUrl(),
                ModelIdentifier.from(dto.getModelIdentifier()),
                dto.getInputs(),
                dto.getOutputs());
    }

    @Override
    public Prediction getPrediction(LIMEExplainabilityRequest request) {
        Map<String, TypedValue> inputs = request.getInputs();
        Map<String, TypedValue> outputs = request.getOutputs();

        PredictionInput input = new PredictionInput(toFeatureList(inputs));
        PredictionOutput output = new PredictionOutput(toOutputList(outputs));
        return new SimplePrediction(input, output);
    }

    @Override
    public BaseExplainabilityResultDto createSucceededResultDto(LIMEExplainabilityRequest request,
            Map<String, Saliency> result) {
        return LIMEExplainabilityResultDto.buildSucceeded(
                request.getExecutionId(),
                result.entrySet().stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new SaliencyDto(e.getValue().getPerFeatureImportance().stream()
                                .map(fi -> new FeatureImportanceDto(fi.getFeature().getName(), fi.getScore()))
                                .collect(Collectors.toList())))));
    }

    @Override
    public BaseExplainabilityResultDto createIntermediateResultDto(LIMEExplainabilityRequest request, Map<String, Saliency> result) {
        throw new UnsupportedOperationException("Intermediate results are not supported by LIME.");
    }

    @Override
    public BaseExplainabilityResultDto createFailedResultDto(LIMEExplainabilityRequest request, Throwable throwable) {
        return LIMEExplainabilityResultDto.buildFailed(request.getExecutionId(), throwable.getMessage());
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
