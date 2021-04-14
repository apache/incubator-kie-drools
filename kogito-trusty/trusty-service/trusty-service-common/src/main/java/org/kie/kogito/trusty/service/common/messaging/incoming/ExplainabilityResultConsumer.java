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

package org.kie.kogito.trusty.service.common.messaging.incoming;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.explainability.api.FeatureImportanceDto;
import org.kie.kogito.explainability.api.LIMEExplainabilityResultDto;
import org.kie.kogito.explainability.api.SaliencyDto;
import org.kie.kogito.trusty.service.common.TrustyService;
import org.kie.kogito.trusty.service.common.messaging.BaseEventConsumer;
import org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.DecisionOutcome;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityStatus;
import org.kie.kogito.trusty.storage.api.model.FeatureImportanceModel;
import org.kie.kogito.trusty.storage.api.model.LIMEExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.SaliencyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;

@ApplicationScoped
public class ExplainabilityResultConsumer extends BaseEventConsumer<BaseExplainabilityResultDto> {

    private static final Logger LOG = LoggerFactory.getLogger(ExplainabilityResultConsumer.class);
    private static final TypeReference<BaseExplainabilityResultDto> CLOUD_EVENT_TYPE = new TypeReference<>() {
    };

    private ExplainabilityResultConsumer() {
        //CDI proxy
    }

    @Inject
    public ExplainabilityResultConsumer(TrustyService service, ObjectMapper mapper) {
        super(service, mapper);
    }

    protected static BaseExplainabilityResult explainabilityResultFrom(BaseExplainabilityResultDto dto, Decision decision) {
        if (dto == null) {
            return null;
        }

        if (dto instanceof LIMEExplainabilityResultDto) {
            LIMEExplainabilityResultDto lime = (LIMEExplainabilityResultDto) dto;
            Map<String, String> outcomeNameToIdMap = decision == null
                    ? Collections.emptyMap()
                    : decision.getOutcomes().stream().collect(Collectors.toUnmodifiableMap(DecisionOutcome::getOutcomeName, DecisionOutcome::getOutcomeId));

            List<SaliencyModel> saliencies = lime.getSaliencies() == null ? null
                    : lime.getSaliencies().entrySet().stream()
                            .map(e -> saliencyFrom(outcomeNameToIdMap.get(e.getKey()), e.getKey(), e.getValue()))
                            .collect(Collectors.toList());
            return new LIMEExplainabilityResult(dto.getExecutionId(), statusFrom(dto.getStatus()), dto.getStatusDetails(), saliencies);
        }

        //TODO {manstis} I need to handle different ExplainabilityResults (see CounterfactualResult too).
        throw new IllegalArgumentException(String.format("Explainability result for '%s' is not supported", dto.getClass().getName()));
    }

    protected static FeatureImportanceModel featureImportanceFrom(FeatureImportanceDto dto) {
        if (dto == null) {
            return null;
        }
        return new FeatureImportanceModel(dto.getFeatureName(), dto.getScore());
    }

    protected static SaliencyModel saliencyFrom(String outcomeId, String outcomeName, SaliencyDto dto) {
        if (dto == null) {
            return null;
        }
        List<FeatureImportanceModel> featureImportanceModel = dto.getFeatureImportance() == null ? null
                : dto.getFeatureImportance().stream()
                        .map(ExplainabilityResultConsumer::featureImportanceFrom)
                        .collect(Collectors.toList());
        return new SaliencyModel(outcomeId, outcomeName, featureImportanceModel);
    }

    protected static ExplainabilityStatus statusFrom(org.kie.kogito.explainability.api.ExplainabilityStatus status) {
        switch (status) {
            case SUCCEEDED:
                return ExplainabilityStatus.SUCCEEDED;
            case FAILED:
                return ExplainabilityStatus.FAILED;
        }
        return null;
    }

    @Override
    @Incoming("trusty-explainability-result")
    public CompletionStage<Void> handleMessage(Message<String> message) {
        return super.handleMessage(message);
    }

    @Override
    protected void internalHandleCloudEvent(CloudEvent cloudEvent, BaseExplainabilityResultDto payload) {
        String executionId = payload.getExecutionId();
        Decision decision = getDecisionById(executionId);
        if (decision == null) {
            LOG.warn("Can't find decision related to explainability result (executionId={})", executionId);
        }
        service.storeExplainabilityResult(executionId, explainabilityResultFrom(payload, decision));
    }

    @Override
    protected TypeReference<BaseExplainabilityResultDto> getEventType() {
        return CLOUD_EVENT_TYPE;
    }

    protected Decision getDecisionById(String executionId) {
        try {
            return service.getDecisionById(executionId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
