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
package org.kie.kogito.eventdriven.decision;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNResult;
import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.decision.DecisionExecutionIdUtils;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.decision.DecisionModels;
import org.kie.kogito.dmn.rest.DMNJSONUtils;
import org.kie.kogito.dmn.rest.KogitoDMNResult;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.DataEventFactory;
import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.event.cloudevents.extension.KogitoExtension;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.kie.kogito.internal.utils.ConversionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.core.provider.ExtensionProvider;

/**
 * This class must always have exact FQCN as <code>org.kie.kogito.eventdriven.decision.EventDrivenDecisionController</code>
 * for code generation plugins to correctly detect if this addon is enabled.
 */
public class EventDrivenDecisionController {

    public static final String REQUEST_EVENT_TYPE = "DecisionRequest";
    public static final String RESPONSE_EVENT_TYPE = "DecisionResponse";
    public static final String RESPONSE_FULL_EVENT_TYPE = "DecisionResponseFull";

    private static final Logger LOG = LoggerFactory.getLogger(EventDrivenDecisionController.class);

    private DecisionModels decisionModels;
    private ConfigBean config;
    private EventEmitter eventEmitter;
    private EventReceiver eventReceiver;

    protected EventDrivenDecisionController() {
    }

    protected EventDrivenDecisionController(DecisionModels decisionModels, ConfigBean config, EventEmitter eventEmitter, EventReceiver eventReceiver) {
        init(decisionModels, config, eventEmitter, eventReceiver);
    }

    protected void init(DecisionModels decisionModels, ConfigBean config, EventEmitter eventEmitter, EventReceiver eventReceiver) {
        this.decisionModels = decisionModels;
        this.config = config;
        this.eventEmitter = eventEmitter;
        this.eventReceiver = eventReceiver;
    }

    protected void subscribe() {
        eventReceiver.subscribe(this::handleRequest, Map.class);
    }

    private CompletionStage<Void> handleRequest(DataEvent<Map> event) {
        KogitoExtension kogitoExtension = ExtensionProvider.getInstance().parseExtension(KogitoExtension.class, event);
        if (CloudEventUtils.isValidRequest(event, REQUEST_EVENT_TYPE, kogitoExtension)) {
            getDecisionModel(kogitoExtension.getDmnModelNamespace(), kogitoExtension.getDmnModelName())
                    .map(model -> processRequest(model, event, kogitoExtension))
                    .ifPresentOrElse(result -> eventEmitter.emit(buildResponseEvent(result, event, kogitoExtension)),
                            () -> LOG.warn("Discarding request because not model is found for {}", kogitoExtension));
        } else {
            LOG.warn("Event {} is not valid. Ignoring it", event);
        }
        return CompletableFuture.completedFuture(null);
    }

    private DataEvent<?> buildResponseEvent(DMNResult result, DataEvent<Map> srcEvent, KogitoExtension extension) {
        URI source = buildResponseCloudEventSource(extension);
        Optional<String> subject = Optional.ofNullable(srcEvent.getSubject());
        KogitoExtension publishedExtension = publishedExtension(extension, result);
        KogitoDMNResult restResult = new KogitoDMNResult(extension.getDmnModelNamespace(), extension.getDmnModelName(), result);
        if (CloudEventUtils.safeBoolean(extension.isDmnFilteredCtx())) {
            restResult.getDmnContext().keySet().removeAll(srcEvent.getData().keySet());
        }
        return CloudEventUtils.safeBoolean(extension.isDmnFullResult()) ? DataEventFactory.from(restResult, RESPONSE_FULL_EVENT_TYPE, source, subject, publishedExtension)
                : DataEventFactory.from(restResult.getDmnContext(), RESPONSE_EVENT_TYPE, source, subject, publishedExtension);
    }

    private DMNResult processRequest(DecisionModel model, DataEvent<Map> event, KogitoExtension kogitoExtension) {
        DMNContext context = DMNJSONUtils.ctx(model, event.getData());
        return ConversionUtils.isEmpty(kogitoExtension.getDmnEvaluateDecision()) ? model.evaluateAll(context)
                : model.evaluateDecisionService(context, kogitoExtension.getDmnEvaluateDecision());
    }

    private Optional<DecisionModel> getDecisionModel(String modelNamespace, String modelName) {
        try {
            return Optional.ofNullable(decisionModels.getDecisionModel(modelNamespace, modelName));
        } catch (IllegalStateException e) {
            LOG.warn("Model not found with name=\"{}\" namespace=\"{}\"", modelName, modelNamespace);
            return Optional.empty();
        }
    }

    private static KogitoExtension publishedExtension(KogitoExtension extension, DMNResult result) {
        KogitoExtension published = new KogitoExtension();
        published.setExecutionId(DecisionExecutionIdUtils.get(result.getContext()));
        published.setDmnModelName(extension.getDmnModelName());
        published.setDmnModelNamespace(extension.getDmnModelNamespace());
        published.setDmnEvaluateDecision(extension.getDmnEvaluateDecision());
        return published;
    }

    private URI buildResponseCloudEventSource(KogitoExtension ctx) {
        return ConversionUtils.isEmpty(ctx.getDmnEvaluateDecision())
                ? CloudEventUtils.buildDecisionSource(config.getServiceUrl(), ctx.getDmnModelName())
                : CloudEventUtils.buildDecisionSource(config.getServiceUrl(), ctx.getDmnModelName(), ctx.getDmnEvaluateDecision());
    }
}
