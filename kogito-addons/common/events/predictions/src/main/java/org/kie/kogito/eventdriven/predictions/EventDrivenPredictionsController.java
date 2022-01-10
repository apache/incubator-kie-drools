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
package org.kie.kogito.eventdriven.predictions;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.kie.api.pmml.PMML4Result;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.event.SubscriptionInfo;
import org.kie.kogito.event.cloudevents.extension.KogitoPredictionsExtension;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.kie.kogito.prediction.PredictionModel;
import org.kie.kogito.prediction.PredictionModelNotFoundException;
import org.kie.kogito.prediction.PredictionModels;
import org.kie.pmml.api.runtime.PMMLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.provider.ExtensionProvider;

public class EventDrivenPredictionsController {

    public static final String REQUEST_EVENT_TYPE = "PredictionRequest";
    public static final String RESPONSE_EVENT_TYPE = "PredictionResponse";
    public static final String RESPONSE_FULL_EVENT_TYPE = "PredictionResponseFull";
    public static final String RESPONSE_ERROR_EVENT_TYPE = "PredictionResponseError";

    private static final Logger LOG = LoggerFactory.getLogger(EventDrivenPredictionsController.class);

    private PredictionModels predictionModels;
    private ConfigBean config;
    private EventEmitter eventEmitter;
    private EventReceiver eventReceiver;

    protected EventDrivenPredictionsController() {
    }

    protected EventDrivenPredictionsController(PredictionModels predictionModels, ConfigBean config, EventEmitter eventEmitter, EventReceiver eventReceiver) {
        this.predictionModels = predictionModels;
        this.config = config;
        this.eventEmitter = eventEmitter;
        this.eventReceiver = eventReceiver;
    }

    protected void init(PredictionModels decisionModels, ConfigBean config, EventEmitter eventEmitter, EventReceiver eventReceiver) {
        this.predictionModels = decisionModels;
        this.config = config;
        this.eventEmitter = eventEmitter;
        this.eventReceiver = eventReceiver;
    }

    protected void subscribe() {
        eventReceiver.subscribe(this::handleRequest,
                new SubscriptionInfo<>(CloudEventUtils.Mapper.mapper()::readValue, CloudEvent.class));
    }

    private CompletionStage<Void> handleRequest(CloudEvent event) {
        filterRequest(event)
                .flatMap(this::buildEvaluationContext)
                .map(this::processRequest)
                .flatMap(this::buildResponseCloudEvent)
                .flatMap(CloudEventUtils::toDataEvent)
                .ifPresent(e -> eventEmitter.emit(e, (String) e.get("type"), Optional.empty()));
        return CompletableFuture.completedFuture(null);
    }

    private Optional<CloudEvent> filterRequest(CloudEvent event) {
        return Optional.ofNullable(event).filter(e -> REQUEST_EVENT_TYPE.equals(e.getType()));
    }

    private Optional<EvaluationContext> buildEvaluationContext(CloudEvent event) {
        KogitoPredictionsExtension extension = ExtensionProvider.getInstance().parseExtension(KogitoPredictionsExtension.class, event);
        Map<String, Object> data = CloudEventUtils.decodeMapData(event, String.class, Object.class).orElse(null);

        if (extension == null) {
            LOG.warn("Received CloudEvent(id={} source={} type={}) with null Kogito extension", event.getId(), event.getSource(), event.getType());
        }

        if (data == null) {
            LOG.warn("Received CloudEvent(id={} source={} type={}) with null data", event.getId(), event.getSource(), event.getType());
        }

        return Optional.of(new EvaluationContext(event, extension, data));
    }

    private EvaluationContext processRequest(EvaluationContext ctx) {
        if (!ctx.isValidRequest()) {
            ctx.setResponseError(PredictionsResponseError.BAD_REQUEST);
            return ctx;
        }

        Optional<PredictionModel> optPredictionModel = getPredictionModel(ctx.getRequestModelName());
        if (!optPredictionModel.isPresent()) {
            ctx.setResponseError(PredictionsResponseError.MODEL_NOT_FOUND);
            return ctx;
        }

        PredictionModel model = optPredictionModel.get();
        PMMLContext context = model.newContext(ctx.getRequestData());
        PMML4Result apiResult = model.evaluateAll(context);

        ctx.setResult(apiResult);
        return ctx;
    }

    private Optional<PredictionModel> getPredictionModel(String modelName) {
        try {
            return Optional.ofNullable(predictionModels.getPredictionModel(modelName));
        } catch (PredictionModelNotFoundException e) {
            LOG.warn("Model not found with name=\"{}\"", modelName);
            return Optional.empty();
        }
    }

    private Optional<CloudEvent> buildResponseCloudEvent(EvaluationContext ctx) {
        String id = UUID.randomUUID().toString();
        URI source = buildResponseCloudEventSource(ctx);
        String subject = ctx.getRequestCloudEvent().getSubject();

        KogitoPredictionsExtension extension = new KogitoPredictionsExtension();
        extension.setPmmlModelName(ctx.getRequestModelName());

        if (ctx.isResponseError()) {
            String data = Optional.ofNullable(ctx.getResponseError()).map(PredictionsResponseError::name).orElse(null);
            return CloudEventUtils.build(id, source, RESPONSE_ERROR_EVENT_TYPE, subject, data, extension);
        }

        if (ctx.isRequestFullResult()) {
            return CloudEventUtils.build(id, source, RESPONSE_FULL_EVENT_TYPE, subject, ctx.getResult(), extension);
        }

        Map<String, Object> data = java.util.Collections.singletonMap(ctx.getResult().getResultObjectName(),
                ctx.getResult().getResultVariables().get(ctx.getResult().getResultObjectName()));

        return CloudEventUtils.build(id, source, RESPONSE_EVENT_TYPE, subject, data, extension);
    }

    private URI buildResponseCloudEventSource(EvaluationContext ctx) {
        return CloudEventUtils.buildDecisionSource(config.getServiceUrl(), ctx.getRequestModelName());
    }

    private static class EvaluationContext {

        private final CloudEvent requestCloudEvent;
        private final Map<String, Object> requestData;

        private final String requestModelName;
        private final boolean requestFullResult;
        private final boolean validRequest;

        private PredictionsResponseError responseError;
        private PMML4Result result;

        public EvaluationContext(CloudEvent requestCloudEvent, KogitoPredictionsExtension requestExtension, Map<String, Object> requestData) {
            this.requestCloudEvent = requestCloudEvent;
            this.requestData = requestData;

            this.requestModelName = Optional.ofNullable(requestExtension)
                    .map(KogitoPredictionsExtension::getPmmlModelName)
                    .orElse(null);
            this.requestFullResult = Optional.ofNullable(requestExtension)
                    .map(KogitoPredictionsExtension::isPmmlFullResult)
                    .orElse(false);

            this.validRequest = requestCloudEvent != null
                    && requestExtension != null
                    && requestModelName != null && !requestModelName.isEmpty()
                    && requestData != null;
        }

        public boolean isValidRequest() {
            return validRequest;
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

        public boolean isRequestFullResult() {
            return requestFullResult;
        }

        public boolean isResponseError() {
            return result == null;
        }

        public PredictionsResponseError getResponseError() {
            return responseError;
        }

        public void setResponseError(PredictionsResponseError responseError) {
            this.responseError = responseError;
        }

        public PMML4Result getResult() {
            return result;
        }

        public void setResult(PMML4Result result) {
            this.result = result;
        }
    }
}
