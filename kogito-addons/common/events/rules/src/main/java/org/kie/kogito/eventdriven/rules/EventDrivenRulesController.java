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
package org.kie.kogito.eventdriven.rules;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.event.cloudevents.extension.KogitoRulesExtension;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.provider.ExtensionProvider;
import io.cloudevents.jackson.JsonCloudEventData;

/**
 * This class must always have exact FQCN as <code>org.kie.kogito.eventdriven.rules.EventDrivenRulesController</code>
 * for code generation plugins to correctly detect if this addon is enabled.
 */
public class EventDrivenRulesController {

    public static final String REQUEST_EVENT_TYPE = "RulesRequest";
    public static final String RESPONSE_EVENT_TYPE = "RulesResponse";
    public static final String RESPONSE_ERROR_EVENT_TYPE = "RulesResponseError";

    private static final Logger LOG = LoggerFactory.getLogger(EventDrivenRulesController.class);

    private Map<String, EventDrivenQueryExecutor> executors;
    private ConfigBean config;
    private EventEmitter eventEmitter;
    private EventReceiver eventReceiver;

    protected EventDrivenRulesController() {
    }

    protected EventDrivenRulesController(Iterable<EventDrivenQueryExecutor> executors, ConfigBean config, EventEmitter eventEmitter, EventReceiver eventReceiver) {
        init(executors, config, eventEmitter, eventReceiver);

    }

    protected void init(Iterable<EventDrivenQueryExecutor> executors, ConfigBean config, EventEmitter eventEmitter, EventReceiver eventReceiver) {
        this.executors = buildExecutorsMap(executors);
        this.config = config;
        this.eventEmitter = eventEmitter;
        this.eventReceiver = eventReceiver;

    }

    protected void subscribe() {
        eventReceiver.subscribe(this::handleRequest, Map.class);
    }

    private CompletionStage<Void> handleRequest(DataEvent<Map> event) {
        validateRequest(event)
                .flatMap(this::buildEvaluationContext)
                .map(this::processRequest)
                .flatMap(this::buildResponseCloudEvent)
                .ifPresent(e -> eventEmitter.emit(e, e.getType(), Optional.empty()));
        return CompletableFuture.completedFuture(null);
    }

    private Optional<DataEvent<Map>> validateRequest(DataEvent<Map> event) {
        return Optional.ofNullable(event).filter(e -> REQUEST_EVENT_TYPE.equals(e.getType()));
    }

    private Optional<EvaluationContext> buildEvaluationContext(DataEvent<Map> event) {
        KogitoRulesExtension extension = ExtensionProvider.getInstance().parseExtension(KogitoRulesExtension.class, event);

        if (extension == null) {
            LOG.warn("Received CloudEvent(id={} source={} type={}) with null Kogito extension", event.getId(), event.getSource(), event.getType());
        }

        if (event.getData() == null) {
            LOG.warn("Received CloudEvent(id={} source={} type={}) with null data", event.getId(), event.getSource(), event.getType());
        }

        return Optional.of(new EvaluationContext(event.asCloudEvent(), extension));
    }

    private EvaluationContext processRequest(EvaluationContext ctx) {
        if (!ctx.isValidRequest()) {
            ctx.setResponseError(RulesResponseError.BAD_REQUEST);
            return ctx;
        }

        Optional<EventDrivenQueryExecutor> optExecutor = getExecutor(ctx.getRuleUnitId(), ctx.getQueryName());
        if (!optExecutor.isPresent()) {
            ctx.setResponseError(RulesResponseError.QUERY_NOT_FOUND);
            return ctx;
        }

        EventDrivenQueryExecutor executor = optExecutor.get();
        try {
            Object result = executor.executeQuery(ctx.getRequestCloudEvent());
            ctx.setQueryResult(result);
        } catch (RuntimeException e) {
            LOG.error("Internal execution error", e);
            ctx.setResponseError(RulesResponseError.INTERNAL_EXECUTION_ERROR);
        }

        return ctx;
    }

    private Optional<EventDrivenQueryExecutor> getExecutor(String ruleUnitId, String queryName) {
        return Optional.ofNullable(executors.get(buildExecutorId(ruleUnitId, queryName)));
    }

    private Optional<CloudEvent> buildResponseCloudEvent(EvaluationContext ctx) {
        String id = UUID.randomUUID().toString();
        URI source = buildResponseCloudEventSource(ctx);
        String subject = ctx.getRequestCloudEvent().getSubject();

        KogitoRulesExtension extension = new KogitoRulesExtension();
        extension.setRuleUnitId(ctx.getRuleUnitId());
        extension.setRuleUnitQuery(ctx.getQueryName());

        if (ctx.isResponseError()) {
            String data = Optional.ofNullable(ctx.getResponseError()).map(RulesResponseError::name).orElse(null);
            return CloudEventUtils.build(id, source, RESPONSE_ERROR_EVENT_TYPE, subject, data, extension);
        }

        return CloudEventUtils.build(id, source, RESPONSE_EVENT_TYPE, subject, ctx.getQueryResult(), extension);
    }

    private URI buildResponseCloudEventSource(EvaluationContext ctx) {
        return CloudEventUtils.buildDecisionSource(config.getServiceUrl(), toKebabCase(ctx.getQueryName()));
    }

    private static String buildExecutorId(String ruleUnitId, String queryName) {
        return String.format("%s#%s", ruleUnitId, queryName);
    }

    private static Map<String, EventDrivenQueryExecutor> buildExecutorsMap(Iterable<EventDrivenQueryExecutor> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toMap(e -> buildExecutorId(e.getRuleUnitId(), e.getQueryName()), e -> e));
    }

    private static String toKebabCase(String inputString) {
        return inputString == null ? null : inputString.replaceAll("(.)(\\p{Upper})", "$1-$2").toLowerCase();
    }

    private static class EvaluationContext {

        private final CloudEvent requestCloudEvent;
        private final String ruleUnitId;
        private final String queryName;
        private final boolean validRequest;

        private RulesResponseError responseError;
        private Object queryResult;

        public EvaluationContext(CloudEvent requestCloudEvent, KogitoRulesExtension requestExtension) {
            this.requestCloudEvent = requestCloudEvent;

            this.ruleUnitId = Optional.ofNullable(requestExtension)
                    .map(KogitoRulesExtension::getRuleUnitId)
                    .orElse(null);
            this.queryName = Optional.ofNullable(requestExtension)
                    .map(KogitoRulesExtension::getRuleUnitQuery)
                    .orElse(null);

            this.validRequest = isValidCloudEvent(requestCloudEvent) && requestExtension != null
                    && ruleUnitId != null && !ruleUnitId.isEmpty()
                    && queryName != null && !queryName.isEmpty();
        }

        public CloudEvent getRequestCloudEvent() {
            return requestCloudEvent;
        }

        public String getRuleUnitId() {
            return ruleUnitId;
        }

        public String getQueryName() {
            return queryName;
        }

        public boolean isValidRequest() {
            return validRequest;
        }

        boolean isResponseError() {
            return queryResult == null;
        }

        public RulesResponseError getResponseError() {
            return responseError;
        }

        public void setResponseError(RulesResponseError responseError) {
            this.responseError = responseError;
        }

        public Object getQueryResult() {
            return queryResult;
        }

        public void setQueryResult(Object queryResult) {
            this.queryResult = queryResult;
        }
    }

    private static boolean isValidCloudEvent(CloudEvent event) {
        if (event == null || event.getData() == null) {
            return false;
        }
        if (event.getData() instanceof JsonCloudEventData) {
            JsonCloudEventData jced = (JsonCloudEventData) event.getData();
            return jced.getNode() != null && !jced.getNode().isNull();
        }
        return true;
    }

}
