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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNResult;
import org.kie.kogito.cloudevents.CloudEventUtils;
import org.kie.kogito.cloudevents.extension.KogitoExtension;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.decision.DecisionExecutionIdUtils;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.decision.DecisionModels;
import org.kie.kogito.dmn.rest.DMNJSONUtils;
import org.kie.kogito.dmn.rest.KogitoDMNResult;
import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.event.SubscriptionInfo;
import org.kie.kogito.services.event.impl.JsonStringToObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.provider.ExtensionProvider;

public class EventDrivenDecisionController {

    public static final String REQUEST_EVENT_TYPE = "DecisionRequest";
    public static final String RESPONSE_EVENT_TYPE = "DecisionResponse";
    public static final String RESPONSE_FULL_EVENT_TYPE = "DecisionResponseFull";
    public static final String RESPONSE_ERROR_EVENT_TYPE = "DecisionResponseError";

    private static final Logger LOG = LoggerFactory.getLogger(EventDrivenDecisionController.class);

    private DecisionModels decisionModels;
    private ConfigBean config;
    private EventEmitter eventEmitter;
    private EventReceiver eventReceiver;

    protected EventDrivenDecisionController() {
    }

    protected EventDrivenDecisionController(DecisionModels decisionModels, ConfigBean config, EventEmitter eventEmitter, EventReceiver eventReceiver) {
        this.decisionModels = decisionModels;
        this.config = config;
        this.eventEmitter = eventEmitter;
        this.eventReceiver = eventReceiver;
    }

    protected void setup(DecisionModels decisionModels, ConfigBean config, EventEmitter eventEmitter, EventReceiver eventReceiver) {
        this.decisionModels = decisionModels;
        this.config = config;
        this.eventEmitter = eventEmitter;
        this.eventReceiver = eventReceiver;
        setup();
    }

    protected void setup() {
        eventReceiver.subscribe(this::handleRequest, new SubscriptionInfo<>(new JsonStringToObject(CloudEventUtils.Mapper.mapper()),
                CloudEvent.class));
    }

    void handleEvent(String event) {
        CloudEventUtils.decode(event)
                .filter(e -> REQUEST_EVENT_TYPE.equals(e.getType()))
                .ifPresent(this::handleRequest);
    }

    private CompletionStage<Void> handleRequest(CloudEvent event) {
        buildEvaluationContext(event)
                .map(this::processRequest)
                .flatMap(this::buildResponseCloudEvent)
                .flatMap(CloudEventUtils::toDataEvent)
                .ifPresent(e -> eventEmitter.emit(e, (String) e.get("type"), Optional.empty()));
        return CompletableFuture.completedFuture(null);
    }

    private Optional<EvaluationContext> buildEvaluationContext(CloudEvent event) {
        if (event == null) {
            LOG.error("Received null CloudEvent");
            return Optional.empty();
        }

        KogitoExtension kogitoExtension = ExtensionProvider.getInstance().parseExtension(KogitoExtension.class, event);
        Map<String, Object> data = CloudEventUtils.decodeMapData(event, String.class, Object.class).orElse(null);

        if (kogitoExtension == null) {
            LOG.warn("Received CloudEvent(id={} source={} type={}) with null Kogito extension", event.getId(), event.getSource(), event.getType());
        }

        if (data == null) {
            LOG.warn("Received CloudEvent(id={} source={} type={}) with null data", event.getId(), event.getSource(), event.getType());
        }

        return Optional.of(new EvaluationContext(event, kogitoExtension, data));
    }

    private EvaluationContext processRequest(EvaluationContext ctx) {
        if (!ctx.isValidRequest()) {
            ctx.setResponseError(DecisionResponseError.BAD_REQUEST);
            return ctx;
        }

        Optional<DecisionModel> optDecisionModel = getDecisionModel(ctx.getRequestModelNamespace(), ctx.getRequestModelName());
        if (!optDecisionModel.isPresent()) {
            ctx.setResponseError(DecisionResponseError.MODEL_NOT_FOUND);
            return ctx;
        }

        DecisionModel model = optDecisionModel.get();
        DMNContext context = DMNJSONUtils.ctx(model, ctx.getRequestData());

        DMNResult apiResult = ctx.isEvaluateDecisionServiceRequest()
                ? model.evaluateDecisionService(context, ctx.getRequestDecisionServiceToEvaluate())
                : model.evaluateAll(context);

        ctx.setResponseDmnResult(apiResult);
        return ctx;
    }

    private Optional<DecisionModel> getDecisionModel(String modelNamespace, String modelName) {
        try {
            return Optional.ofNullable(decisionModels.getDecisionModel(modelNamespace, modelName));
        } catch (IllegalStateException e) {
            LOG.warn("Model not found with name=\"{}\" namespace=\"{}\"", modelName, modelNamespace);
            return Optional.empty();
        }
    }

    private Optional<CloudEvent> buildResponseCloudEvent(EvaluationContext ctx) {
        String id = UUID.randomUUID().toString();
        URI source = buildResponseCloudEventSource(ctx);
        String subject = ctx.getRequestCloudEvent().getSubject();

        KogitoExtension kogitoExtension = new KogitoExtension();
        kogitoExtension.setDmnModelName(ctx.getRequestModelName());
        kogitoExtension.setDmnModelNamespace(ctx.getRequestModelNamespace());
        kogitoExtension.setDmnEvaluateDecision(ctx.getRequestDecisionServiceToEvaluate());

        if (ctx.isResponseError()) {
            String data = Optional.ofNullable(ctx.getResponseError()).map(DecisionResponseError::name).orElse(null);
            return CloudEventUtils.build(id, source, RESPONSE_ERROR_EVENT_TYPE, subject, data, kogitoExtension);
        }

        kogitoExtension.setExecutionId(DecisionExecutionIdUtils.get(ctx.getResponseDmnResult().getContext()));

        KogitoDMNResult restResult = new KogitoDMNResult(ctx.getRequestModelNamespace(), ctx.getRequestModelName(), ctx.getResponseDmnResult());

        if (ctx.isRequestFullResult()) {
            if (ctx.isRequestFilteredContext()) {
                restResult.setDmnContext(filterContext(restResult.getDmnContext(), ctx.requestData));
            }
            return CloudEventUtils.build(id, source, RESPONSE_FULL_EVENT_TYPE, subject, restResult, kogitoExtension);
        }

        Map<String, Object> data = ctx.isRequestFilteredContext()
                ? filterContext(restResult.getDmnContext(), ctx.requestData)
                : restResult.getDmnContext();

        return CloudEventUtils.build(id, source, RESPONSE_EVENT_TYPE, subject, data, kogitoExtension);
    }

    private Map<String, Object> filterContext(Map<String, Object> values, Map<String, Object> inputs) {
        return values.entrySet().stream()
                .filter(entry -> !inputs.containsKey(entry.getKey()))
                .collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), HashMap::putAll);
    }

    private URI buildResponseCloudEventSource(EvaluationContext ctx) {
        return ctx.isEvaluateDecisionServiceRequest()
                ? CloudEventUtils.buildDecisionSource(config.getServiceUrl(), ctx.getRequestModelName(), ctx.getRequestDecisionServiceToEvaluate())
                : CloudEventUtils.buildDecisionSource(config.getServiceUrl(), ctx.getRequestModelName());
    }

    private static class EvaluationContext {

        private final CloudEvent requestCloudEvent;
        private final Map<String, Object> requestData;

        private final String requestModelName;
        private final String requestModelNamespace;
        private final String requestDecisionServiceToEvaluate;
        private final boolean requestFullResult;
        private final boolean requestFilteredContext;
        private final boolean validRequest;
        private final boolean evaluateDecisionServiceRequest;

        private DecisionResponseError responseError;
        private DMNResult responseDmnResult;

        public EvaluationContext(CloudEvent requestCloudEvent, KogitoExtension requestKogitoExtension, Map<String, Object> requestData) {
            this.requestCloudEvent = requestCloudEvent;
            this.requestData = requestData;

            this.requestModelName = Optional.ofNullable(requestKogitoExtension)
                    .map(KogitoExtension::getDmnModelName)
                    .orElse(null);
            this.requestModelNamespace = Optional.ofNullable(requestKogitoExtension)
                    .map(KogitoExtension::getDmnModelNamespace)
                    .orElse(null);
            this.requestDecisionServiceToEvaluate = Optional.ofNullable(requestKogitoExtension)
                    .map(KogitoExtension::getDmnEvaluateDecision)
                    .orElse(null);
            this.requestFullResult = Optional.ofNullable(requestKogitoExtension)
                    .map(KogitoExtension::isDmnFullResult)
                    .orElse(false);
            this.requestFilteredContext = Optional.ofNullable(requestKogitoExtension)
                    .map(KogitoExtension::isDmnFilteredCtx)
                    .orElse(false);

            this.validRequest = requestCloudEvent != null
                    && requestKogitoExtension != null
                    && requestModelName != null && !requestModelName.isEmpty()
                    && requestModelNamespace != null && !requestModelNamespace.isEmpty()
                    && requestData != null;

            this.evaluateDecisionServiceRequest = validRequest
                    && requestDecisionServiceToEvaluate != null
                    && !requestDecisionServiceToEvaluate.isEmpty();
        }

        public boolean isValidRequest() {
            return validRequest;
        }

        public boolean isEvaluateDecisionServiceRequest() {
            return evaluateDecisionServiceRequest;
        }

        public CloudEvent getRequestCloudEvent() {
            return requestCloudEvent;
        }

        public Map<String, Object> getRequestData() {
            return requestData;
        }

        String getRequestModelName() {
            return requestModelName;
        }

        String getRequestModelNamespace() {
            return requestModelNamespace;
        }

        String getRequestDecisionServiceToEvaluate() {
            return requestDecisionServiceToEvaluate;
        }

        public boolean isRequestFullResult() {
            return requestFullResult;
        }

        public boolean isRequestFilteredContext() {
            return requestFilteredContext;
        }

        boolean isResponseError() {
            return responseDmnResult == null;
        }

        public DecisionResponseError getResponseError() {
            return responseError;
        }

        public void setResponseError(DecisionResponseError responseError) {
            this.responseError = responseError;
        }

        public DMNResult getResponseDmnResult() {
            return responseDmnResult;
        }

        public void setResponseDmnResult(DMNResult responseDmnResult) {
            this.responseDmnResult = responseDmnResult;
        }
    }
}
