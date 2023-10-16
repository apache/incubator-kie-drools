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
package org.kie.kogito.trusty.service.common.messaging.incoming;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.tracing.event.model.ModelEvent;
import org.kie.kogito.tracing.event.model.models.DecisionModelEvent;
import org.kie.kogito.trusty.service.common.TrustyService;
import org.kie.kogito.trusty.service.common.messaging.BaseEventConsumer;
import org.kie.kogito.trusty.storage.api.StorageExceptionsProvider;
import org.kie.kogito.trusty.storage.api.model.decision.DMNModelMetadata;
import org.kie.kogito.trusty.storage.api.model.decision.DMNModelWithMetadata;
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

    protected ModelEventConsumer() {
        //CDI proxy
    }

    @Inject
    public ModelEventConsumer(TrustyService service,
            ObjectMapper mapper,
            StorageExceptionsProvider storageExceptionsProvider,
            ManagedExecutor executor) {
        super(service,
                mapper,
                storageExceptionsProvider,
                executor);
    }

    @Override
    @Incoming("kogito-tracing-model")
    public CompletionStage<Void> handleMessage(final Message<String> message) {
        return CompletableFuture.runAsync(() -> super.handleMessage(message), executor);
    }

    @Override
    protected TypeReference<ModelEvent> getEventType() {
        return CLOUD_EVENT_TYPE;
    }

    @Override
    protected void internalHandleCloudEvent(CloudEvent cloudEvent, ModelEvent payload) {
        final org.kie.kogito.event.ModelMetadata modelMetadata = payload.getModelMetadata();
        switch (modelMetadata.getModelDomain()) {
            case DECISION:
                internalHandleDecisionModelEvent((DecisionModelEvent) payload);
                break;
            default:
                LOG.error("Unsupported ModelMetadata type {}", modelMetadata.getModelDomain());
        }
    }

    private void internalHandleDecisionModelEvent(DecisionModelEvent decisionModelEvent) {
        DMNModelMetadata identifier = new DMNModelMetadata(decisionModelEvent.getGav().getGroupId(),
                decisionModelEvent.getGav().getArtifactId(),
                decisionModelEvent.getGav().getVersion(),
                decisionModelEvent.getModelMetadata().getSpecVersion(),
                decisionModelEvent.getName(),
                decisionModelEvent.getNamespace());
        DMNModelWithMetadata dmnModelWithMetadata = new DMNModelWithMetadata(identifier,
                decisionModelEvent.getDefinition());
        service.storeModel(dmnModelWithMetadata);
    }
}
