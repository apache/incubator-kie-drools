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

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.core.type.TypeReference;
import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.CloudEventImpl;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.decision.DecisionModelType;
import org.kie.kogito.tracing.decision.event.model.ModelEvent;
import org.kie.kogito.trusty.service.TrustyService;
import org.kie.kogito.trusty.service.messaging.BaseEventConsumer;

@ApplicationScoped
public class ModelEventConsumer extends BaseEventConsumer<ModelEvent> {

    private static final TypeReference<CloudEventImpl<ModelEvent>> CLOUD_EVENT_TYPE_REF = new TypeReference<>() {
    };

    private  ModelEventConsumer() {
        //CDI proxy
    }

    @Inject
    public ModelEventConsumer(final TrustyService service) {
        super(service);
    }

    @Override
    @Incoming("kogito-tracing-model")
    public CompletionStage<Void> handleMessage(final Message<String> message) {
        return super.handleMessage(message);
    }

    @Override
    protected TypeReference<CloudEventImpl<ModelEvent>> getCloudEventType() {
        return CLOUD_EVENT_TYPE_REF;
    }

    @Override
    protected void handleCloudEvent(final CloudEventImpl<ModelEvent> cloudEvent) {
        final AttributesImpl attributes = cloudEvent.getAttributes();
        final Optional<ModelEvent> optData = cloudEvent.getData();

        if (!optData.isPresent()) {
            LOG.error("Received CloudEvent with id {} from {} with empty data", attributes.getId(), attributes.getSource());
            return;
        }

        LOG.debug("Received CloudEvent with id {} from {}", attributes.getId(), attributes.getSource());

        final ModelEvent modelEvent = optData.get();
        final DecisionModelType modelEventType = modelEvent.getType();

        if (modelEventType == DecisionModelType.DMN) {
            service.storeModel(modelEvent.getGav().getGroupId(),
                               modelEvent.getGav().getArtifactId(),
                               modelEvent.getGav().getVersion(),
                               modelEvent.getName(),
                               modelEvent.getNamespace(),
                               ModelEventConverter.toModel(modelEvent));
        } else {
            LOG.error("Unsupported DecisionModelType type {}", modelEventType);
        }
    }
}
