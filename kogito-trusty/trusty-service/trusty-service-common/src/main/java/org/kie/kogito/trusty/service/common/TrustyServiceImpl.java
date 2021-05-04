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

package org.kie.kogito.trusty.service.common;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequestDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainDto;
import org.kie.kogito.explainability.api.LIMEExplainabilityRequestDto;
import org.kie.kogito.explainability.api.ModelIdentifierDto;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.QueryFilterFactory;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.trusty.service.common.handlers.ExplainerServiceHandlerRegistry;
import org.kie.kogito.trusty.service.common.messaging.MessagingUtils;
import org.kie.kogito.trusty.service.common.messaging.incoming.ModelIdentifier;
import org.kie.kogito.trusty.service.common.messaging.outgoing.ExplainabilityRequestProducer;
import org.kie.kogito.trusty.service.common.models.MatchedExecutionHeaders;
import org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityRequest;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomain;
import org.kie.kogito.trusty.storage.api.model.DMNModelWithMetadata;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.Execution;
import org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue;
import org.kie.kogito.trusty.storage.common.TrustyStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.orderBy;
import static org.kie.kogito.persistence.api.query.SortDirection.DESC;

@ApplicationScoped
public class TrustyServiceImpl implements TrustyService {

    private static final Logger LOG = LoggerFactory.getLogger(TrustyServiceImpl.class);

    private boolean isExplainabilityEnabled;

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
            ExplainerServiceHandlerRegistry explainerServiceHandlerRegistry) {
        this.isExplainabilityEnabled = Boolean.TRUE.equals(isExplainabilityEnabled);
        this.explainabilityRequestProducer = explainabilityRequestProducer;
        this.storageService = storageService;
        this.explainerServiceHandlerRegistry = explainerServiceHandlerRegistry;
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
            Map<String, TypedValue> inputs = decision.getInputs() != null
                    ? decision.getInputs().stream()
                            .collect(HashMap::new, (m, v) -> m.put(v.getName(), MessagingUtils.modelToTracingTypedValue(v.getValue())), HashMap::putAll)
                    : Collections.emptyMap();

            Map<String, TypedValue> outputs = decision.getOutcomes() != null
                    ? decision.getOutcomes().stream()
                            .collect(HashMap::new, (m, v) -> m.put(v.getOutcomeName(), MessagingUtils.modelToTracingTypedValue(v.getOutcomeResult())), HashMap::putAll)
                    : Collections.emptyMap();

            explainabilityRequestProducer.sendEvent(new LIMEExplainabilityRequestDto(
                    executionId,
                    decision.getServiceUrl(),
                    createDecisionModelIdentifierDto(decision),
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
    public void storeModel(ModelIdentifier modelIdentifier, DMNModelWithMetadata dmnModelWithMetadata) {
        final Storage<String, DMNModelWithMetadata> storage = storageService.getModelStorage();
        if (storage.containsKey(modelIdentifier.getIdentifier())) {
            throw new IllegalArgumentException(String.format("A model with ID %s is already present in the storage.", modelIdentifier.getIdentifier()));
        }
        storage.put(modelIdentifier.getIdentifier(), dmnModelWithMetadata);
    }

    @Override
    public DMNModelWithMetadata getModelById(ModelIdentifier modelIdentifier) {
        final Storage<String, DMNModelWithMetadata> storage = storageService.getModelStorage();
        if (!storage.containsKey(modelIdentifier.getIdentifier())) {
            throw new IllegalArgumentException(String.format("A model with ID %s does not exist in the storage.", modelIdentifier.getIdentifier()));
        }
        return storage.get(modelIdentifier.getIdentifier());
    }

    @Override
    public CounterfactualExplainabilityRequest requestCounterfactuals(String executionId,
            List<TypedVariableWithValue> goals,
            List<CounterfactualSearchDomain> searchDomains) {
        Storage<String, Decision> storage = storageService.getDecisionsStorage();
        if (!storage.containsKey(executionId)) {
            throw new IllegalArgumentException(String.format("A decision with ID %s is not present in the storage. Counterfactuals cannot be requested.", executionId));
        }
        CounterfactualExplainabilityRequest counterfactualRequest = storeCounterfactualRequest(executionId, goals, searchDomains);
        sendCounterfactualRequestEvent(executionId, counterfactualRequest.getCounterfactualId(), goals, searchDomains);

        return counterfactualRequest;
    }

    protected CounterfactualExplainabilityRequest storeCounterfactualRequest(String executionId,
            List<TypedVariableWithValue> goals,
            List<CounterfactualSearchDomain> searchDomains) {
        String counterfactualId = UUID.randomUUID().toString();
        CounterfactualExplainabilityRequest counterfactualRequest = new CounterfactualExplainabilityRequest(executionId, counterfactualId, goals, searchDomains);
        Storage<String, CounterfactualExplainabilityRequest> storage = storageService.getCounterfactualRequestStorage();
        storage.put(counterfactualId, counterfactualRequest);

        return counterfactualRequest;
    }

    protected void sendCounterfactualRequestEvent(String executionId,
            String counterfactualId,
            List<TypedVariableWithValue> goals,
            List<CounterfactualSearchDomain> searchDomains) {
        Decision decision = getDecisionById(executionId);

        Map<String, TypedValue> originalInputs = decision.getInputs() != null
                ? decision.getInputs().stream()
                        .collect(HashMap::new, (m, v) -> m.put(v.getName(), MessagingUtils.modelToTracingTypedValue(v.getValue())), HashMap::putAll)
                : Collections.emptyMap();

        Map<String, TypedValue> requiredOutputs = goals != null
                ? goals.stream()
                        .collect(HashMap::new, (m, v) -> m.put(v.getName(), MessagingUtils.modelToTracingTypedValue(v)), HashMap::putAll)
                : Collections.emptyMap();

        Map<String, CounterfactualSearchDomainDto> searchDomainDtos = searchDomains != null
                ? searchDomains.stream()
                        .collect(HashMap::new, (m, v) -> m.put(v.getName(), MessagingUtils.modelToCounterfactualSearchDomainDto(v)), HashMap::putAll)
                : Collections.emptyMap();

        explainabilityRequestProducer.sendEvent(new CounterfactualExplainabilityRequestDto(
                executionId,
                counterfactualId,
                decision.getServiceUrl(),
                createDecisionModelIdentifierDto(decision),
                originalInputs,
                requiredOutputs,
                searchDomainDtos));
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
        Storage<String, CounterfactualExplainabilityRequest> storage = storageService.getCounterfactualRequestStorage();

        AttributeFilter<String> filterExecutionId = QueryFilterFactory.equalTo(CounterfactualExplainabilityRequest.EXECUTION_ID_FIELD, executionId);
        AttributeFilter<String> filterCounterfactualId = QueryFilterFactory.equalTo(CounterfactualExplainabilityRequest.COUNTERFACTUAL_ID_FIELD, counterfactualId);
        List<AttributeFilter<?>> filters = List.of(filterExecutionId, filterCounterfactualId);
        List<CounterfactualExplainabilityRequest> counterfactuals = storage.query().filter(filters).execute();

        if (counterfactuals.isEmpty()) {
            throw new IllegalArgumentException(String.format("Counterfactual for Execution Id '%s' and Counterfactual Id '%s' does not exist in the storage.", executionId, counterfactualId));
        }
        if (counterfactuals.size() > 1) {
            throw new IllegalArgumentException(String.format("Multiple Counterfactuals for Execution Id '%s' and Counterfactual Id '%s' found in the storage.", executionId, counterfactualId));
        }

        return counterfactuals.get(0);
    }

    private ModelIdentifierDto createDecisionModelIdentifierDto(Decision decision) {
        String resourceId = decision.getExecutedModelNamespace() +
                ModelIdentifierDto.RESOURCE_ID_SEPARATOR +
                decision.getExecutedModelName();
        return new ModelIdentifierDto("dmn", resourceId);
    }

}
