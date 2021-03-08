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

import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.decision.DecisionModelMetadata;
import org.kie.kogito.tracing.decision.event.model.ModelEvent;
import org.kie.kogito.trusty.service.common.TrustyService;
import org.kie.kogito.trusty.service.common.messaging.BaseEventConsumer;
import org.kie.kogito.trusty.storage.api.model.DMNModelWithMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;

@ApplicationScoped
public class ModelEventConsumer extends BaseEventConsumer<ModelEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(ModelEventConsumer.class);
    private static final TypeReference<ModelEvent> CLOUD_EVENT_TYPE = new TypeReference<>() {
    };

    private ModelEventConsumer() {
        //CDI proxy
    }

    @Inject
    public ModelEventConsumer(final TrustyService service, ObjectMapper mapper) {
        super(service, mapper);
    }

    @Override
    @Incoming("kogito-tracing-model")
    public CompletionStage<Void> handleMessage(final Message<String> message) {
        return super.handleMessage(message);
    }

    @Override
    protected TypeReference<ModelEvent> getEventType() {
        return CLOUD_EVENT_TYPE;
    }

    @Override
    protected void internalHandleCloudEvent(CloudEvent cloudEvent, ModelEvent payload) {
        final DecisionModelMetadata decisionModelMetadata = payload.getDecisionModelMetadata();
        if (decisionModelMetadata.getType().equals(DecisionModelMetadata.Type.DMN)) {
            ModelIdentifier identifier = new ModelIdentifier(payload.getGav().getGroupId(),
                    payload.getGav().getArtifactId(),
                    payload.getGav().getVersion(),
                    payload.getName(),
                    payload.getNamespace());
            DMNModelWithMetadata dmnModelWithMetadata = DMNModelWithMetadata.fromCloudEvent(payload);
            service.storeModel(identifier, dmnModelWithMetadata);
        } else {
            LOG.error("Unsupported DecisionModelType type {}", decisionModelMetadata.getType());
        }
    }
}
