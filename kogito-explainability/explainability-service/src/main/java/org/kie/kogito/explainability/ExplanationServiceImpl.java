/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.explainability;

import org.kie.kogito.explainability.api.ExplainabilityResultDto;
import org.kie.kogito.explainability.api.FeatureImportanceDto;
import org.kie.kogito.explainability.api.SaliencyDto;
import org.kie.kogito.explainability.local.LocalExplainer;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.models.ExplainabilityRequest;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static org.kie.kogito.explainability.ConversionUtils.toFeatureList;
import static org.kie.kogito.explainability.ConversionUtils.toOutputList;

@ApplicationScoped
public class ExplanationServiceImpl implements ExplanationService {

    private static final Logger LOG = LoggerFactory.getLogger(ExplanationServiceImpl.class);

    private final LocalExplainer<Map<String, Saliency>> localExplainer;

    @Inject
    public ExplanationServiceImpl(
        LocalExplainer<Map<String, Saliency>> localExplainer) {
        this.localExplainer = localExplainer;
    }

    @Override
    public CompletionStage<ExplainabilityResultDto> explainAsync(
            ExplainabilityRequest request,
            PredictionProvider predictionProvider) {
        LOG.debug("Explainability request with executionId {} for model {}:{}",
                request.getExecutionId(),
                request.getModelIdentifier().getResourceType(),
                request.getModelIdentifier().getResourceId());
        Prediction prediction = getPrediction(request.getInputs(), request.getOutputs());
        return localExplainer.explainAsync(prediction, predictionProvider)
                .thenApply(input -> createResultDto(input, request.getExecutionId()))
                .exceptionally(throwable -> {
                    LOG.error("Exception thrown during explainAsync", throwable);
                    return new ExplainabilityResultDto(request.getExecutionId(), Collections.emptyMap());
                });
    }

    protected static ExplainabilityResultDto createResultDto(Map<String, Saliency> saliencies, String executionId) {
        return new ExplainabilityResultDto(
                executionId,
                saliencies.entrySet().stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new SaliencyDto(e.getValue().getPerFeatureImportance().stream()
                                .map(fi -> new FeatureImportanceDto(fi.getFeature().getName(), fi.getScore()))
                                .collect(Collectors.toList())
                        )
                ))
        );
    }

    protected static Prediction getPrediction(Map<String, TypedValue> inputs, Map<String, TypedValue> outputs) {
        PredictionInput input = getPredictionInput(inputs);
        PredictionOutput output = getPredictionOutput(outputs);
        return new Prediction(input, output);
    }

    protected static PredictionInput getPredictionInput(Map<String, TypedValue> inputs) {
        return new PredictionInput(toFeatureList(inputs));
    }

    protected static PredictionOutput getPredictionOutput(Map<String, TypedValue> outputs) {
        return new PredictionOutput(toOutputList(outputs));
    }
}
