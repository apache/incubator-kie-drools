/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.trusty.service.messaging.incoming;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.core.type.TypeReference;
import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.CloudEventImpl;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.explainability.api.ExplainabilityResultDto;
import org.kie.kogito.explainability.api.FeatureImportanceDto;
import org.kie.kogito.explainability.api.SaliencyDto;
import org.kie.kogito.trusty.service.TrustyService;
import org.kie.kogito.trusty.service.messaging.BaseEventConsumer;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.FeatureImportance;
import org.kie.kogito.trusty.storage.api.model.Saliency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ExplainabilityResultConsumer extends BaseEventConsumer<ExplainabilityResultDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExplainabilityResultConsumer.class);

    private static final TypeReference<CloudEventImpl<ExplainabilityResultDto>> CLOUD_EVENT_TYPE = new TypeReference<>() {
    };

    private ExplainabilityResultConsumer() {
        //CDI proxy
    }

    @Inject
    public ExplainabilityResultConsumer(TrustyService service) {
        super(service);
    }

    @Override
    @Incoming("trusty-explainability-result")
    public CompletionStage<Void> handleMessage(Message<String> message) {
        return super.handleMessage(message);
    }

    @Override
    protected TypeReference<CloudEventImpl<ExplainabilityResultDto>> getCloudEventType() {
        return CLOUD_EVENT_TYPE;
    }

    @Override
    protected void handleCloudEvent(CloudEventImpl<ExplainabilityResultDto> cloudEvent) {
        AttributesImpl attributes = cloudEvent.getAttributes();
        Optional<ExplainabilityResultDto> optData = cloudEvent.getData();

        if (!optData.isPresent()) {
            LOGGER.error("Received CloudEvent with id {} from {} with empty data", attributes.getId(), attributes.getSource());
            return;
        }

        LOGGER.info("Received CloudEvent with id {} from {}", attributes.getId(), attributes.getSource());

        ExplainabilityResultDto explainabilityResult = optData.get();

        service.storeExplainabilityResult(attributes.getId(), explainabilityResultFrom(explainabilityResult));
    }

    protected static ExplainabilityResult explainabilityResultFrom(ExplainabilityResultDto dto) {
        if (dto == null) {
            return null;
        }
        Map<String, Saliency> saliencies = dto.getSaliencies() == null ? null :
                dto.getSaliencies().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> saliencyFrom(e.getValue())));
        return new ExplainabilityResult(dto.getExecutionId(), saliencies);
    }

    protected static FeatureImportance featureImportanceFrom(FeatureImportanceDto dto) {
        if (dto == null) {
            return null;
        }
        return new FeatureImportance(dto.getFeatureId(), dto.getScore());
    }

    protected static Saliency saliencyFrom(SaliencyDto dto) {
        if (dto == null) {
            return null;
        }
        List<FeatureImportance> featureImportance = dto.getFeatureImportance() == null ? null :
                dto.getFeatureImportance().stream()
                        .map(ExplainabilityResultConsumer::featureImportanceFrom)
                        .collect(Collectors.toList());
        return new Saliency(featureImportance);
    }
}

