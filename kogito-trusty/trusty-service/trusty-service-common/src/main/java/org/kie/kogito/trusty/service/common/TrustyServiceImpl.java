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
package org.kie.kogito.trusty.service.common;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.explainability.api.BaseExplainabilityResult;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.explainability.api.CounterfactualSearchDomain;
import org.kie.kogito.explainability.api.LIMEExplainabilityRequest;
import org.kie.kogito.explainability.api.ModelIdentifier;
import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.QueryFilterFactory;
import org.kie.kogito.trusty.service.common.handlers.ExplainerServiceHandlerRegistry;
import org.kie.kogito.trusty.service.common.messaging.outgoing.ExplainabilityRequestProducer;
import org.kie.kogito.trusty.service.common.models.MatchedExecutionHeaders;
import org.kie.kogito.trusty.storage.api.model.Execution;
import org.kie.kogito.trusty.storage.api.model.ModelMetadata;
import org.kie.kogito.trusty.storage.api.model.ModelWithMetadata;
import org.kie.kogito.trusty.storage.api.model.decision.Decision;
import org.kie.kogito.trusty.storage.api.model.decision.DecisionInput;
import org.kie.kogito.trusty.storage.api.model.decision.DecisionOutcome;
import org.kie.kogito.trusty.storage.common.TrustyStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import static java.util.Arrays.asList;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.orderBy;
import static org.kie.kogito.persistence.api.query.SortDirection.DESC;
import static org.kie.kogito.trusty.service.common.CounterfactualParameterValidation.isStructureIdentical;
import static org.kie.kogito.trusty.service.common.CounterfactualParameterValidation.isStructureSubset;

@ApplicationScoped
public class TrustyServiceImpl implements TrustyService {

    private static final Logger LOG = LoggerFactory.getLogger(TrustyServiceImpl.class);

    private boolean isExplainabilityEnabled;
    private Long maxRunningTimeSeconds;

    private ExplainabilityRequestProducer explainabilityRequestProducer;
    private TrustyStorageService storageService;
    private ExplainerServiceHandlerRegistry explainerServiceHandlerRegistry;

    TrustyServiceImpl() {
        // dummy constructor needed
    }

    @Inject
    public TrustyServiceImpl(
            @ConfigProperty(name = "trusty.explainability.enabled") Boolean isExplainabilityEnabled,
            ExplainabilityRequestProducer explainabilityRequestProducer,
            TrustyStorageService storageService,
            ExplainerServiceHandlerRegistry explainerServiceHandlerRegistry,
            @ConfigProperty(name = "trusty.explainability.counterfactuals.maxRunningTimeSeconds",
                    defaultValue = "60") Long maxRunningTimeSeconds) {
        this.isExplainabilityEnabled = Boolean.TRUE.equals(isExplainabilityEnabled);
        this.explainabilityRequestProducer = explainabilityRequestProducer;
        this.storageService = storageService;
        this.explainerServiceHandlerRegistry = explainerServiceHandlerRegistry;
        this.maxRunningTimeSeconds = maxRunningTimeSeconds;
    }

    // used only in tests
    void enableExplainability() {
        isExplainabilityEnabled = true;
    }

    @Override
    public MatchedExecutionHeaders getExecutionHeaders(OffsetDateTime from, OffsetDateTime to, int limit, int offset, String prefix) {
        Storage<String, Decision> storage = storageService.getDecisionsStorage();
        List<AttributeFilter<?>> filters = new ArrayList<>();
        filters.add(QueryFilterFactory.like(Execution.EXECUTION_ID_FIELD, prefix + "*"));
        filters.add(QueryFilterFactory.greaterThanEqual(Execution.EXECUTION_TIMESTAMP_FIELD, from.toInstant().toEpochMilli()));
        filters.add(QueryFilterFactory.lessThanEqual(Execution.EXECUTION_TIMESTAMP_FIELD, to.toInstant().toEpochMilli()));
        ArrayList result = new ArrayList<>(storage.query()
                .sort(asList(orderBy(Execution.EXECUTION_TIMESTAMP_FIELD, DESC)))
                .filter(filters)
                .execute());

        if (result.size() < offset) {
            throw new IllegalArgumentException("Out of bound start offset in result");
        }

        return new MatchedExecutionHeaders(result.subList(offset, Math.min(offset + limit, result.size())), result.size());
    }

    @Override
    public void storeDecision(String executionId, Decision decision) {
        Storage<String, Decision> storage = storageService.getDecisionsStorage();
        if (storage.containsKey(executionId)) {
            throw new IllegalArgumentException(String.format("A decision with ID %s is already present in the storage.", executionId));
        }
        storage.put(executionId, decision);
    }

    @Override
    public Decision getDecisionById(String executionId) {
        Storage<String, Decision> storage = storageService.getDecisionsStorage();
        if (!storage.containsKey(executionId)) {
            throw new IllegalArgumentException(String.format("A decision with ID %s does not exist in the storage.", executionId));
        }
        return storage.get(executionId);
    }

    @Override
    public void updateDecision(String executionId, Decision decision) {
        storageService.getDecisionsStorage().put(executionId, decision);
    }

    @Override
    public void processDecision(String executionId, Decision decision) {
        storeDecision(executionId, decision);

        if (isExplainabilityEnabled) {
            List<NamedTypedValue> inputs = decision.getInputs() != null
                    ? decision.getInputs().stream()
                            .map(input -> new NamedTypedValue(input.getName(), input.getValue()))
                            .collect(Collectors.toList())
                    : Collections.emptyList();

            List<NamedTypedValue> outputs = decision.getOutcomes() != null
                    ? decision.getOutcomes().stream()
                            .map(output -> new NamedTypedValue(output.getOutcomeName(), output.getOutcomeResult()))
                            .collect(Collectors.toList())
                    : Collections.emptyList();

            explainabilityRequestProducer.sendEvent(new LIMEExplainabilityRequest(
                    executionId,
                    decision.getServiceUrl(),
                    createDecisionModelIdentifier(decision),
                    inputs,
                    outputs));
        }
    }

    @Override
    public <T extends BaseExplainabilityResult> void storeExplainabilityResult(String executionId, T result) {
        explainerServiceHandlerRegistry.storeExplainabilityResult(executionId, result);
    }

    @Override
    public <T extends BaseExplainabilityResult> T getExplainabilityResultById(String executionId, Class<T> type) {
        return explainerServiceHandlerRegistry.getExplainabilityResultById(executionId, type);
    }

    @Override
    public <T extends ModelMetadata, E extends ModelWithMetadata<T>> void storeModel(E modelWithMetadata) {
        final Storage<String, E> storage = storageService.getModelStorage((Class<E>) modelWithMetadata.getClass());
        if (storage.containsKey(modelWithMetadata.getIdentifier())) {
            throw new IllegalArgumentException(String.format("A model with ID %s is already present in the storage.",
                    modelWithMetadata.getIdentifier()));
        }
        storage.put(modelWithMetadata.getIdentifier(), modelWithMetadata);
    }

    @Override
    public <T extends ModelMetadata, E extends ModelWithMetadata<T>> E getModelById(T modelMetadata, Class<E> modelWithMetadataClass) {
        final Storage<String, E> storage = storageService.getModelStorage(modelWithMetadataClass);
        if (!storage.containsKey(modelMetadata.getIdentifier())) {
            throw new IllegalArgumentException(String.format("A model with ID %s does not exist in the storage.", modelMetadata.getIdentifier()));
        }
        return storage.get(modelMetadata.getIdentifier());
    }

    @Override
    public CounterfactualExplainabilityRequest requestCounterfactuals(String executionId,
            List<NamedTypedValue> goals,
            List<CounterfactualSearchDomain> searchDomains) {
        Storage<String, Decision> storage = storageService.getDecisionsStorage();
        if (!storage.containsKey(executionId)) {
            throw new IllegalArgumentException(String.format("A decision with ID %s is not present in the storage. Counterfactuals cannot be requested.", executionId));
        }

        CounterfactualExplainabilityRequest request = makeCounterfactualRequest(executionId, goals, searchDomains, maxRunningTimeSeconds);
        storeCounterfactualRequest(request);
        sendCounterfactualRequestEvent(request);

        return request;
    }

    protected CounterfactualExplainabilityRequest makeCounterfactualRequest(String executionId,
            List<NamedTypedValue> goals,
            List<CounterfactualSearchDomain> searchDomains,
            Long maxRunningTimeSeconds) {
        Decision decision = getDecisionById(executionId);

        //This is returned as null under Redis, so play safe
        Collection<DecisionInput> decisionInputs = Objects.nonNull(decision.getInputs()) ? decision.getInputs() : Collections.emptyList();
        if (!isStructureIdentical(decisionInputs, searchDomains)) {
            String error = buildCounterfactualErrorMessage(String.format("The structure of the Search Domains do not match the structure of the original Inputs for decision with ID %s.", executionId),
                    "Decision inputs:-", decisionInputs,
                    "Search domains:-", searchDomains);
            LOG.error(error);
            throw new IllegalArgumentException(error);
        }

        //This is returned as null under Redis, so play safe
        Collection<DecisionOutcome> decisionOutcomes = Objects.nonNull(decision.getOutcomes()) ? decision.getOutcomes() : Collections.emptyList();
        if (!isStructureSubset(decisionOutcomes, goals)) {
            String error =
                    buildCounterfactualErrorMessage(String.format("The structure of the Goals is not comparable to the structure of the original Outcomes for decision with ID %s.", executionId),
                            "Decision outcomes:-", decisionOutcomes,
                            "Goals:-", goals);
            LOG.error(error);
            throw new IllegalArgumentException(error);
        }

        List<NamedTypedValue> cfInputs = decision.getInputs() != null
                ? decision.getInputs().stream()
                        .map(input -> new NamedTypedValue(input.getName(), input.getValue()))
                        .collect(Collectors.toList())
                : Collections.emptyList();

        List<NamedTypedValue> cfGoals = goals != null
                ? goals
                : Collections.emptyList();

        List<CounterfactualSearchDomain> cfSearchDomains = searchDomains != null
                ? searchDomains
                : Collections.emptyList();

        return new CounterfactualExplainabilityRequest(
                executionId,
                decision.getServiceUrl(),
                createDecisionModelIdentifier(decision),
                UUID.randomUUID().toString(),
                cfInputs,
                cfGoals,
                cfSearchDomains,
                maxRunningTimeSeconds);
    }

    protected void storeCounterfactualRequest(CounterfactualExplainabilityRequest request) {
        Storage<String, CounterfactualExplainabilityRequest> storage = storageService.getCounterfactualRequestStorage();
        storage.put(request.getCounterfactualId(), request);
    }

    protected void sendCounterfactualRequestEvent(CounterfactualExplainabilityRequest request) {
        explainabilityRequestProducer.sendEvent(request);
    }

    private <T> String buildCounterfactualErrorMessage(String title,
            String decisionValuesTitle,
            Object decisionValues,
            String counterfactualValuesTitle,
            List<T> counterfactualValues) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        StringBuilder sb = new StringBuilder(title).append("\n");
        try {
            sb.append(decisionValuesTitle).append("\n").append(writer.writeValueAsString(decisionValues)).append("\n");
            sb.append(counterfactualValuesTitle).append("\n").append(writer.writeValueAsString(counterfactualValues)).append("\n");
        } catch (JsonProcessingException jpe) {
            //Swallow
        }
        return sb.toString();
    }

    @Override
    public List<CounterfactualExplainabilityRequest> getCounterfactualRequests(String executionId) {
        Storage<String, CounterfactualExplainabilityRequest> storage = storageService.getCounterfactualRequestStorage();

        AttributeFilter<String> filterExecutionId = QueryFilterFactory.equalTo(CounterfactualExplainabilityRequest.EXECUTION_ID_FIELD, executionId);
        List<CounterfactualExplainabilityRequest> counterfactuals = storage.query().filter(Collections.singletonList(filterExecutionId)).execute();

        return List.copyOf(counterfactuals);
    }

    @Override
    public CounterfactualExplainabilityRequest getCounterfactualRequest(String executionId, String counterfactualId) {
        List<CounterfactualExplainabilityRequest> requests = getCounterfactualsFromStorage(executionId,
                counterfactualId,
                storageService.getCounterfactualRequestStorage());

        if (requests.isEmpty()) {
            throw new IllegalArgumentException(String.format("Counterfactual for Execution Id '%s' and Counterfactual Id '%s' does not exist in the storage.", executionId, counterfactualId));
        }
        if (requests.size() > 1) {
            throw new IllegalArgumentException(String.format("Multiple Counterfactuals for Execution Id '%s' and Counterfactual Id '%s' found in the storage.", executionId, counterfactualId));
        }

        return requests.get(0);
    }

    @Override
    public List<CounterfactualExplainabilityResult> getCounterfactualResults(String executionId, String counterfactualId) {
        return getCounterfactualsFromStorage(executionId,
                counterfactualId,
                storageService.getCounterfactualResultStorage());
    }

    private <T> List<T> getCounterfactualsFromStorage(String executionId, String counterfactualId, Storage<String, T> storage) {
        AttributeFilter<String> filterExecutionId = QueryFilterFactory.equalTo(CounterfactualExplainabilityRequest.EXECUTION_ID_FIELD, executionId);
        AttributeFilter<String> filterCounterfactualId = QueryFilterFactory.equalTo(CounterfactualExplainabilityRequest.COUNTERFACTUAL_ID_FIELD, counterfactualId);
        List<AttributeFilter<?>> filters = List.of(filterExecutionId, filterCounterfactualId);
        List<T> result = storage.query().filter(filters).execute();

        return Objects.nonNull(result) ? result : Collections.emptyList();
    }

    private ModelIdentifier createDecisionModelIdentifier(Decision decision) {
        String resourceId = decision.getExecutedModelNamespace() +
                ModelIdentifier.RESOURCE_ID_SEPARATOR +
                decision.getExecutedModelName();
        return new ModelIdentifier("dmn", resourceId);
    }
}
