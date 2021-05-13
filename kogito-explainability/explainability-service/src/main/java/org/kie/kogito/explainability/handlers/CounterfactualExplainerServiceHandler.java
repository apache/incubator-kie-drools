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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.explainability.ConversionUtils;
import org.kie.kogito.explainability.api.BaseExplainabilityRequestDto;
import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequestDto;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResultDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainCollectionDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainStructureDto;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualExplainer;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualResult;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntity;
import org.kie.kogito.explainability.model.CounterfactualPrediction;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionFeatureDomain;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.models.BaseExplainabilityRequest;
import org.kie.kogito.explainability.models.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.models.ModelIdentifier;
import org.kie.kogito.tracing.typedvalue.CollectionValue;
import org.kie.kogito.tracing.typedvalue.StructureValue;
import org.kie.kogito.tracing.typedvalue.TypedValue;

import static org.kie.kogito.explainability.ConversionUtils.toFeatureConstraintList;
import static org.kie.kogito.explainability.ConversionUtils.toFeatureDomainList;
import static org.kie.kogito.explainability.ConversionUtils.toFeatureList;
import static org.kie.kogito.explainability.ConversionUtils.toOutputList;

@ApplicationScoped
public class CounterfactualExplainerServiceHandler
        implements LocalExplainerServiceHandler<CounterfactualResult, CounterfactualExplainabilityRequest, CounterfactualExplainabilityRequestDto> {

    private final CounterfactualExplainer explainer;

    @Inject
    public CounterfactualExplainerServiceHandler(CounterfactualExplainer explainer) {
        this.explainer = explainer;
    }

    @Override
    public <T extends BaseExplainabilityRequest> boolean supports(Class<T> type) {
        return CounterfactualExplainabilityRequest.class.isAssignableFrom(type);
    }

    @Override
    public <T extends BaseExplainabilityRequestDto> boolean supportsDto(Class<T> type) {
        return CounterfactualExplainabilityRequestDto.class.isAssignableFrom(type);
    }

    @Override
    public CounterfactualExplainabilityRequest explainabilityRequestFrom(CounterfactualExplainabilityRequestDto dto) {
        return new CounterfactualExplainabilityRequest(
                dto.getExecutionId(),
                dto.getCounterfactualId(),
                dto.getServiceUrl(),
                ModelIdentifier.from(dto.getModelIdentifier()),
                dto.getInputs(),
                dto.getOutputs(),
                dto.getSearchDomains());
    }

    @Override
    public Prediction getPrediction(CounterfactualExplainabilityRequest request) {
        Map<String, TypedValue> originalInputs = request.getInputs();
        Map<String, TypedValue> requiredOutputs = request.getOutputs();
        Map<String, CounterfactualSearchDomainDto> searchDomains = request.getSearchDomains();

        // If the incoming is not flat we cannot perform CF on it so fail fast
        // See https://issues.redhat.com/browse/FAI-473 and https://issues.redhat.com/browse/FAI-474
        if (isUnsupportedModel(originalInputs, requiredOutputs, searchDomains)) {
            throw new IllegalArgumentException("Counterfactual explanations only support flat models.");
        }

        PredictionInput input = new PredictionInput(toFeatureList(originalInputs));
        PredictionOutput output = new PredictionOutput(toOutputList(requiredOutputs));
        PredictionFeatureDomain featureDomain = new PredictionFeatureDomain(toFeatureDomainList(searchDomains));
        List<Boolean> featureConstraints = toFeatureConstraintList(searchDomains);

        return new CounterfactualPrediction(input,
                output,
                featureDomain,
                featureConstraints,
                null,
                UUID.fromString(request.getExecutionId()));
    }

    private boolean isUnsupportedModel(Map<String, TypedValue> originalInputs,
            Map<String, TypedValue> requiredOutputs,
            Map<String, CounterfactualSearchDomainDto> searchDomains) {
        return isUnsupportedTypedValue(originalInputs.values())
                || isUnsupportedTypedValue(requiredOutputs.values())
                || isUnsupportedCounterfactualSearchDomain(searchDomains.values());
    }

    private boolean isUnsupportedTypedValue(Collection<TypedValue> typedValues) {
        return typedValues.stream().anyMatch(tv -> tv instanceof StructureValue || tv instanceof CollectionValue);
    }

    private boolean isUnsupportedCounterfactualSearchDomain(Collection<CounterfactualSearchDomainDto> domains) {
        return domains.stream().anyMatch(domain -> domain instanceof CounterfactualSearchDomainStructureDto
                || domain instanceof CounterfactualSearchDomainCollectionDto);
    }

    @Override
    public BaseExplainabilityResultDto createSucceededResultDto(CounterfactualExplainabilityRequest request,
            CounterfactualResult result) {
        return buildResultDtoFromExplanation(request, result);
    }

    @Override
    public BaseExplainabilityResultDto createFailedResultDto(CounterfactualExplainabilityRequest request, Throwable throwable) {
        return CounterfactualExplainabilityResultDto.buildFailed(request.getExecutionId(),
                request.getCounterfactualId(),
                throwable.getMessage());
    }

    @Override
    public BaseExplainabilityResultDto createIntermediateResultDto(CounterfactualExplainabilityRequest request, CounterfactualResult result) {
        return buildResultDtoFromExplanation(request, result);
    }

    private CounterfactualExplainabilityResultDto buildResultDtoFromExplanation(CounterfactualExplainabilityRequest request,
            CounterfactualResult result) {
        List<Feature> features = result.getEntities().stream().map(CounterfactualEntity::asFeature).collect(Collectors.toList());
        List<PredictionOutput> predictionOutputs = result.getOutput();
        if (Objects.isNull(predictionOutputs)) {
            throw new NullPointerException(String.format("Null Outputs produced for Explanation with ExecutionId '%s' and CounterfactualId '%s'",
                    request.getExecutionId(),
                    request.getCounterfactualId()));
        } else if (predictionOutputs.isEmpty()) {
            throw new IllegalStateException(String.format("No Outputs produced for Explanation with ExecutionId '%s' and CounterfactualId '%s'",
                    request.getExecutionId(),
                    request.getCounterfactualId()));
        } else if (predictionOutputs.size() > 1) {
            throw new IllegalStateException(String.format("Multiple Output sets produced for Explanation with ExecutionId '%s' and CounterfactualId '%s'",
                    request.getExecutionId(),
                    request.getCounterfactualId()));
        }

        List<Output> outputs = predictionOutputs.get(0).getOutputs();
        return CounterfactualExplainabilityResultDto.buildSucceeded(request.getExecutionId(),
                request.getCounterfactualId(),
                result.getSolutionId().toString(),
                result.isValid(),
                CounterfactualExplainabilityResultDto.Stage.FINAL,
                ConversionUtils.fromFeatureList(features),
                ConversionUtils.fromOutputs(outputs));
    }

    @Override
    public CompletableFuture<CounterfactualResult> explainAsync(Prediction prediction,
            PredictionProvider predictionProvider,
            Consumer<CounterfactualResult> intermediateResultsConsumer) {
        return explainer.explainAsync(prediction,
                predictionProvider,
                intermediateResultsConsumer);
    }
}
