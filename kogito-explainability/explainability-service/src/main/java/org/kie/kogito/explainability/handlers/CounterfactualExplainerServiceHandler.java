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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.explainability.ConversionUtils;
import org.kie.kogito.explainability.PredictionProviderFactory;
import org.kie.kogito.explainability.api.BaseExplainabilityRequest;
import org.kie.kogito.explainability.api.BaseExplainabilityResult;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.explainability.api.CounterfactualSearchDomain;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainCollectionValue;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainStructureValue;
import org.kie.kogito.explainability.api.HasNameValue;
import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualExplainer;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualResult;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntity;
import org.kie.kogito.explainability.model.CounterfactualPrediction;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.tracing.typedvalue.CollectionValue;
import org.kie.kogito.tracing.typedvalue.StructureValue;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.explainability.ConversionUtils.toFeatureList;
import static org.kie.kogito.explainability.ConversionUtils.toOutputList;

@ApplicationScoped
public class CounterfactualExplainerServiceHandler
        implements LocalExplainerServiceHandler<CounterfactualResult, CounterfactualExplainabilityRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CounterfactualExplainerServiceHandler.class);

    private final Long kafkaMaxRecordAgeSeconds;

    private final CounterfactualExplainer explainer;
    private final PredictionProviderFactory predictionProviderFactory;

    @Inject
    public CounterfactualExplainerServiceHandler(CounterfactualExplainer explainer,
            PredictionProviderFactory predictionProviderFactory,
            @ConfigProperty(name = "mp.messaging.incoming.trusty-explainability-request.throttled.unprocessed-record-max-age.ms", defaultValue = "60000") Long kafkaMaxRecordAgeMilliSeconds) {
        this.explainer = explainer;
        this.predictionProviderFactory = predictionProviderFactory;
        this.kafkaMaxRecordAgeSeconds = Math.floorDiv(kafkaMaxRecordAgeMilliSeconds, 1000);
    }

    @Override
    public <T extends BaseExplainabilityRequest> boolean supports(Class<T> type) {
        return CounterfactualExplainabilityRequest.class.isAssignableFrom(type);
    }

    @Override
    public PredictionProvider getPredictionProvider(CounterfactualExplainabilityRequest request) {
        return predictionProviderFactory.createPredictionProvider(request.getServiceUrl(),
                request.getModelIdentifier(),
                request.getGoals());
    }

    @Override
    public Prediction getPrediction(CounterfactualExplainabilityRequest request) {
        Collection<NamedTypedValue> goals = toMapBasedSorting(request.getGoals());
        Collection<CounterfactualSearchDomain> searchDomains = request.getSearchDomains();
        Collection<NamedTypedValue> originalInputs = request.getOriginalInputs();
        Long maxRunningTimeSeconds = request.getMaxRunningTimeSeconds();

        if (Objects.nonNull(maxRunningTimeSeconds)) {
            if (maxRunningTimeSeconds > kafkaMaxRecordAgeSeconds) {
                LOGGER.info(String.format("Maximum Running Timeout set to '%d's since the provided value '%d's exceeded the Messaging sub-system configuration '%d's.", kafkaMaxRecordAgeSeconds,
                        maxRunningTimeSeconds, kafkaMaxRecordAgeSeconds));
                maxRunningTimeSeconds = kafkaMaxRecordAgeSeconds;
            }
        }

        // If the incoming is not flat we cannot perform CF on it so fail fast
        // See https://issues.redhat.com/browse/FAI-473 and https://issues.redhat.com/browse/FAI-474
        if (isUnsupportedModel(originalInputs, goals, searchDomains)) {
            throw new IllegalArgumentException("Counterfactual explanations only support flat models.");
        }

        PredictionInput input = new PredictionInput(toFeatureList(originalInputs, searchDomains));

        PredictionOutput output = new PredictionOutput(toOutputList(goals));

        return new CounterfactualPrediction(input,
                output,
                null,
                UUID.fromString(request.getExecutionId()),
                maxRunningTimeSeconds);
    }

    private boolean isUnsupportedModel(Collection<NamedTypedValue> originalInputs,
            Collection<NamedTypedValue> goals,
            Collection<CounterfactualSearchDomain> searchDomains) {
        return isUnsupportedTypedValue(originalInputs)
                || isUnsupportedTypedValue(goals)
                || isUnsupportedCounterfactualSearchDomain(searchDomains);
    }

    private boolean isUnsupportedTypedValue(Collection<? extends HasNameValue<?>> values) {
        return values.stream().map(HasNameValue::getValue).anyMatch(tv -> tv instanceof StructureValue || tv instanceof CollectionValue);
    }

    private boolean isUnsupportedCounterfactualSearchDomain(Collection<CounterfactualSearchDomain> domains) {
        return domains.stream().map(CounterfactualSearchDomain::getValue).anyMatch(domain -> domain instanceof CounterfactualSearchDomainStructureValue
                || domain instanceof CounterfactualSearchDomainCollectionValue);
    }

    private List<NamedTypedValue> toMapBasedSorting(Collection<NamedTypedValue> goals) {
        // When the Prediction is run its Outcomes are placed in a HashMap. The iteration order of the HashMap's
        // members is different to the iteration of the List containing the Goals; which contains the original sequencing
        // of Outcomes from execution of the original Decision through to the UI, Counterfactual request and receipt here.
        // To ensure the ordering is correct for CounterFactualScoreCalculator.calculateScore(..) and
        // CounterFactualScoreCalculator.outputDistance(..) we need to perform the same re-ordering
        // i.e write to HashMap and read back to a List.
        // See https://issues.redhat.com/browse/FAI-653
        Map<String, TypedValue> goalsMap = goals != null
                ? goals.stream()
                        .collect(HashMap::new, (m, v) -> m.put(v.getName(), v.getValue()), HashMap::putAll)
                : Collections.emptyMap();
        return goalsMap.entrySet()
                .stream()
                .map(e -> new NamedTypedValue(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public BaseExplainabilityResult createSucceededResult(CounterfactualExplainabilityRequest request,
            CounterfactualResult result) {
        return buildResultFromExplanation(request, result, CounterfactualExplainabilityResult.Stage.FINAL);
    }

    @Override
    public BaseExplainabilityResult createFailedResult(CounterfactualExplainabilityRequest request, Throwable throwable) {
        return CounterfactualExplainabilityResult.buildFailed(request.getExecutionId(),
                request.getCounterfactualId(),
                throwable.getMessage());
    }

    @Override
    public BaseExplainabilityResult createIntermediateResult(CounterfactualExplainabilityRequest request, CounterfactualResult result) {
        return buildResultFromExplanation(request, result, CounterfactualExplainabilityResult.Stage.INTERMEDIATE);
    }

    private CounterfactualExplainabilityResult buildResultFromExplanation(CounterfactualExplainabilityRequest request,
            CounterfactualResult result,
            CounterfactualExplainabilityResult.Stage stage) {
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
        return CounterfactualExplainabilityResult.buildSucceeded(request.getExecutionId(),
                request.getCounterfactualId(),
                result.getSolutionId().toString(),
                result.getSequenceId(),
                result.isValid(),
                stage,
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
